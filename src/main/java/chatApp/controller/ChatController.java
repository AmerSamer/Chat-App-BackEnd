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
import java.util.List;

import static chatApp.Utilities.ExceptionMessages.*;
import static chatApp.Utilities.SuccessMessages.*;

@RestController
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public ResponseEntity<CustomResponse<Message>> sendMainPlainMessage(Message message) {
        try {
            Message resMessage = messageService.addMessageToMainChat(message);
            CustomResponse<Message> response = new CustomResponse<>(resMessage, mainMessageSentSuccessfully);
            return ResponseEntity.ok().body(response);
        } catch (IllegalAccessException e) {
            CustomResponse<Message> response = new CustomResponse<>(null, userIsMutedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @MessageMapping("/plain/privatechat/{roomId}")
    @SendTo("/topic/privatechat/{roomId}")
    public ResponseEntity<CustomResponse<Message>> sendPrivatePlainMessage(Message message) {
        Message resMessage = messageService.addMessageToPrivateChat(message);
        CustomResponse<Message> response = new CustomResponse<>(resMessage, privateMessageSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/getusers", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<UserDTO>>> getAllUsers() {
        List<User> userList = userService.getAllUsers();
        List<UserDTO> userListDTO = UserDTO.userListToUserListDTO(userList);
        CustomResponse<List<UserDTO>> response = new CustomResponse<>(userListDTO, listOfAllUsersSuccessfulMessage);
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/privatechatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> getPrivateRoom(@RequestParam("sender") String senderEmail,
                                                                         @RequestParam("receiver") Long receiverId) {
        List<Message> messageList = messageService.getPrivateRoomMessages(senderEmail, receiverId);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, privateChatRoomMessagesSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }
    @RequestMapping(value = "/mainchatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> getMainRoom(@RequestParam("size") int size) {
        List<Message> messageList = messageService.getMainRoomMessages(size);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, mainChatRoomMessagesSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/downloadprivatechatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> downloadPrivateRoom(@RequestParam("roomId") String roomId) {
        List<Message> messageList = messageService.downloadPrivateRoomMessages(roomId);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, downloadPrivateRoomSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/downloadmainchatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> downloadMainRoom(@RequestParam("date") String date,
                                                                           @RequestParam("time") String time) {
        List<Message> messageList = messageService.getMainRoomMessagesByTime(date, time);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, downloadMainRoomSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }


}