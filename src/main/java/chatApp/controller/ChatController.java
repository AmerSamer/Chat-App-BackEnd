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

import java.sql.SQLDataException;
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

    /**
     * sends the message to the addMessageToMainChat method in the messageService
     * @param message - the message's data
     * @return a saved message
     */
    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public Message sendPlainMessage(Message message) {
        return messageService.addMessageToMainChat(message);
    }

    /**
     * sends the message to the addMessageToPrivateChat method in the messageService
     * @param message - the message's data
     * @return a saved message
     */
    @MessageMapping("/plain/privatechat/{roomId}")
    @SendTo("/topic/privatechat/{roomId}")
    public Message sendPrivatePlainMessage(Message message) {
        return messageService.addMessageToPrivateChat(message);
    }

    /**
     * calling the getAllUsers method in the userService
     * @return list of all users
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<UserDTO>>> getAllUsers() {
        List<User> userList = userService.getAllUsers();
        List<UserDTO> userListDTO = userListToUserListDTO(userList);
        CustomResponse<List<UserDTO>> response = new CustomResponse<>(userListDTO, listOfAllUsersSuccessfulMessage);
        return ResponseEntity.ok().body(response);
    }

    /**
     * sends the senderEmail, receiverId to the getPrivateRoomMessages method in the messageService
     * @param senderEmail - the Email of the sender
     * @param receiverId - the id of the receiver
     * @return list of messages of private chat room
     */
    @RequestMapping(value = "/privatechatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> getPrivateRoom(@RequestParam("sender") String senderEmail,
                                                                         @RequestParam("receiver") Long receiverId) {
        List<Message> messageList = messageService.getPrivateRoomMessages(senderEmail, receiverId);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, "success");
        return ResponseEntity.ok().body(response);
    }
//    @RequestMapping(value = "/mainchatroomexport", method = RequestMethod.GET)
//    private ResponseEntity<CustomResponse<List<Message>>> getMainRoom(@RequestParam("time") int time) {
//        List<Message> messageList = messageService.getMainRoomMessages(time);
//        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, "success");
//        return ResponseEntity.ok().body(response);
//    }
    /**
     * sends the size to the getMainRoomMessages method in the messageService
     * @param size - the number of returned rows
     * @return list of messages of main chat room
     */
    @RequestMapping(value = "/mainchatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> getMainRoom(@RequestParam("size") int size) {
        List<Message> messageList = messageService.getMainRoomMessages(size);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, "success");
        return ResponseEntity.ok().body(response);
    }

    /**
     * sends the roomId to the downloadPrivateRoomMessages method in the messageService
     * @param roomId - the room id
     * @return list of messages of specific private chat room
     */
    @RequestMapping(value = "/downloadprivatechatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> getPrivateRoom(@RequestParam("roomId") String roomId) {
        List<Message> messageList = messageService.downloadPrivateRoomMessages(roomId);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, "success");
        return ResponseEntity.ok().body(response);
    }
}