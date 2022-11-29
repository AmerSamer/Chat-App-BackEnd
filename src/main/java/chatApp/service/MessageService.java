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

    /**
     * finding the room id by the userEmail and the receiverId combination
     * @param userEmail - user email to get the roomId
     * @param receiverId - user id to get the roomId
     * @return list of messages of specific private room
     */
    public List<Message> getPrivateRoomMessages(String userEmail, Long receiverId){
        logger.info("Try to get private chat room messages");
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

    /**
     * adds message to private chat room to the db
     * @param message - the message`s data
     * @return saved message
     */
    public Message addMessageToPrivateChat(Message message) {
        logger.info("Try to add message to private chat room");
        message.setIssueDate(LocalDateTime.now());
        return messageRepository.save(message);
    }

    /**
     * downloads private room messages
     * @param roomId - the roomId`s data
     * @return list of messages
     */
    public List<Message> downloadPrivateRoomMessages(String roomId) {
        logger.info("Try to download private chat room messages");
        return messageRepository.findByRoomId(roomId);
    }

    /**
     * adding message to db
     * @param message - the message`s data
     * @return a saved message body
     */
    public Message addMessageToMainChat(Message message) {
        logger.info("Try to add message to main chat room");
        message.setIssueDate(LocalDateTime.now());
        message.setReceiver("null");
        return messageRepository.save(message);
    }

    /**
     * find all the main chat room messages in the db
     * @param size - the number of returned rows
     * @return list of messages sorted by DESC timestamp
     */
    public List<Message> getMainRoomMessages(int size) {
        logger.info("Try to get main chat room messages");
        return messageRepository.findByRoomId("0", PageRequest.of(0, size, Sort.Direction.DESC, "id"));
    }
}
