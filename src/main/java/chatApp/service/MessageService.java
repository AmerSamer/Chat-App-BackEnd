package chatApp.service;


import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin
@Service
public class MessageService {

    private static Logger logger = LogManager.getLogger(MessageService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getPrivateRoomMessages(String userEmail, Long receiverId){
        User senderUser = userRepository.findByEmail(userEmail);
        User receiverUser = userRepository.getById(receiverId);
        Long senderId = senderUser.getId();
        List<Message> messageList =  messageRepository.findByRoomId(senderId + "E" + receiverId);
        if(messageList.isEmpty()){
            messageList =  messageRepository.findByRoomId(receiverId + "E" + senderId);
            if(messageList.isEmpty()){
                messageList.add(messageRepository.save(new Message(userEmail, "New Private Chat Room" , receiverUser.getEmail(), receiverId + "E" + senderId)));
            }
        }
        return messageList;
    }

    public Message addMessageToPrivateChat(Message message) {
        message.setIssueDate(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> downloadPrivateRoomMessages(String roomId) {
        return messageRepository.findByRoomId(roomId);
    }

    public Message addMessageToMainChat(Message message) {
        message.setIssueDate(LocalDateTime.now());
        message.setReceiver("null");
        return messageRepository.save(message);
    }

    public List<Message> getMainRoomMessages(int size) {
        return messageRepository.findByRoomId("0", PageRequest.of(0, size, Sort.Direction.DESC, "id"));
    }

//    public List<Message> getMainRoomMessagessssssss(int time) {
//        return messageRepository.findByRoomIdAndIssueDateBetween("0", LocalDateTime.now().minusHours(time), LocalDateTime.now());
//    }
}
