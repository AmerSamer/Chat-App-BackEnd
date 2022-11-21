package chatApp.controller;

import chatApp.entities.Message;
import chatApp.entities.User;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

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
    public Message sendPlainMessage(Message message) {
        return message;
    }
}