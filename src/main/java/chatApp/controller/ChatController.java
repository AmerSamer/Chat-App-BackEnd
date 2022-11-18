package chatApp.controller;

import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;

@RestController
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {
//
//    @Autowired
//    private ChatRoomService chatRoomService;

    @MessageMapping("/hello")
    @SendTo("/topic/mainChat")
    public Message greeting(Message.HelloMessage message) throws Exception {
        return new Message("SYSTEM", message.getName() + "joined the chat");
    }

    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public Message sendPlainMessage(User user, Message message) {
        return message;
    }


//    @RequestMapping(method = RequestMethod.POST)
//    public String updateChatRoom(@RequestBody User user){
//        try {
//            return userService.addUser(user).toString();
//        } catch (SQLDataException e) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST, "Email already exists", e);
//        }
//    }

}