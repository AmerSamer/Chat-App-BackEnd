package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.service.MessageService;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static chatApp.Utilities.SuccessMessages.listOfAllUsersSuccessfulMessage;
import static chatApp.Utilities.Utility.userListToUserListDTO;

@RestController
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/hello")
    @SendTo("/topic/mainChat")
    public Message greeting(Message.HelloMessage message) {
        return new Message("SYSTEM", message.getName() + "joined the chat");
    }
//    @MessageMapping("/hello")
//    @SendTo("/topic/mainChat")
//    public ResponseEntity<CustomResponse<List<Message>>> getMainRoom(Message message) {
//        List<Message> messageList = messageService.getMainRoomMessages(message);
//        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, "success");
//        return ResponseEntity.ok().body(response);
//    }

    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public Message sendPlainMessage(Message message) {
        return messageService.addMessageToMainChat(message);
    }

    @MessageMapping("/plain/privatechat/{roomId}")
    @SendTo("/topic/privatechat/{roomId}")
    public Message sendPrivatePlainMessage(Message message) {
        return messageService.addMessageToPrivateChat(message);
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
        List<Message> messageList = messageService.getPrivateRoomMessages(senderEmail, receiverId);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, "success");
        return ResponseEntity.ok().body(response);
    }
    @RequestMapping(value = "/mainchatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> getMainRoom(@RequestParam("size") int size) {
        List<Message> messageList = messageService.getMainRoomMessages(size);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, "success");
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/downloadprivatechatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> downloadPrivateRoom(@RequestParam("roomId") String roomId) {
        List<Message> messageList = messageService.downloadPrivateRoomMessages(roomId);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, "success");
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/downloadmainchatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> downloadMainRoom(@RequestParam("date") String date,
                                                                           @RequestParam("time") String time) {
        List<Message> messageList = messageService.getMainRoomMessagesByTime(date, time);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, "success");
        return ResponseEntity.ok().body(response);
    }


}