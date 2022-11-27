package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static chatApp.Utilities.SuccessMessages.listOfAllUsersSuccessfulMessage;
import static chatApp.Utilities.Utility.userListToUserListDTO;

@RestController
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private UserService userService;

    @MessageMapping("/hello")
    @SendTo("/topic/mainChat")
    public Message greeting(Message.HelloMessage message) throws Exception {
        return new Message("SYSTEM", message.getName() + "joined the chat");
    }

    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public Message sendPlainMessage(Message message) {
        return message;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<UserDTO>>> getAllUsers() {
        List<User> userList = userService.getAllUsers();
        List<UserDTO> userListDTO = userListToUserListDTO(userList);
        CustomResponse<List<UserDTO>> response = new CustomResponse<>(userListDTO, listOfAllUsersSuccessfulMessage);
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/privatechatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> getPrivateRoom(@RequestParam("sender") String senderEmail,
                                                                         @RequestParam("receiver") Long receiverId) {
        List<Message> messageList = userService.getPrivateRoomMessages(senderEmail, receiverId);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, "stam message");
        return ResponseEntity.ok().body(response);
    }

}