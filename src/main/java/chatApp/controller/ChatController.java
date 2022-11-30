package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.service.MessageService;
import chatApp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static chatApp.Utilities.ExceptionMessages.*;
import static chatApp.Utilities.SuccessMessages.*;

@RestController
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {
    private static Logger logger = LogManager.getLogger(ChatController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    /**
     * sends the message to the addMessageToMainChat method in the messageService
     *
     * @param message - the message's data
     * @return a saved message
     */
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

    /**
     * sends the message to the addMessageToPrivateChat method in the messageService
     *
     * @param message - the message's data
     * @return a saved message
     */
    @MessageMapping("/plain/privatechat/{roomId}")
    @SendTo("/topic/privatechat/{roomId}")
    public ResponseEntity<CustomResponse<Message>> sendPrivatePlainMessage(Message message) {
        Message resMessage = messageService.addMessageToPrivateChat(message);
        CustomResponse<Message> response = new CustomResponse<>(resMessage, privateMessageSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }

    /**
     * calling the getAllUsers method in the userService
     *
     * @return list of all users
     */
    @RequestMapping(value = "/getusers", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<UserDTO>>> getAllUsers() {
        logger.info("Try to get all users to display in the frontend");
        List<User> userList = userService.getAllUsers();
        List<UserDTO> userListDTO = UserDTO.userListToUserListDTO(userList);
        CustomResponse<List<UserDTO>> response = new CustomResponse<>(userListDTO, listOfAllUsersSuccessfulMessage);
        logger.info(listOfAllUsersSuccessfulMessage);
        return ResponseEntity.ok().body(response);
    }

    /**
     * sends the senderEmail, receiverId to the getPrivateRoomMessages method in the messageService
     *
     * @param senderEmail - the Email of the sender
     * @param receiverId  - the id of the receiver
     * @return list of messages of private chat room
     */
    @RequestMapping(value = "/privatechatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> getPrivateRoom(@RequestParam("sender") String senderEmail,
                                                                         @RequestParam("receiver") Long receiverId) {
        logger.info("Try to get private chat room");
        List<Message> messageList = messageService.getPrivateRoomMessages(senderEmail, receiverId);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, privateChatRoomMessagesSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }

    /**
     * sends the size to the getMainRoomMessages method in the messageService
     *
     * @param size - the number of returned rows
     * @return list of messages of main chat room
     */
    @RequestMapping(value = "/mainchatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> getMainRoom(@RequestParam("size") int size) {
        logger.info("Try to get main chat room");
        List<Message> messageList = messageService.getMainRoomMessages(size);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, mainChatRoomMessagesSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }

    /**
     * sends the roomId to the downloadPrivateRoomMessages method in the messageService
     *
     * @param roomId - the room id
     * @return list of messages of specific private chat room
     */
    @RequestMapping(value = "/downloadprivatechatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> downloadPrivateRoom(@RequestParam("roomId") String roomId) {
        logger.info("Try to download specific private chat room");
        List<Message> messageList = messageService.downloadPrivateRoomMessages(roomId);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, downloadPrivateRoomSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/downloadmainchatroom", method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<List<Message>>> downloadMainRoom(@RequestParam("time") long time) {
        List<Message> messageList = messageService.getMainRoomMessagesByTime(time);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageList, downloadMainRoomSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }
}