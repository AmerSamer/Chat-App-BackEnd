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

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import static chatApp.utilities.Utility.*;

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
     *
     * @param userEmail  - user email to get the roomId
     * @param receiverId - user id to get the roomId
     * @return list of messages of specific private room
     */
    public List<Message> getPrivateRoomMessages(String userEmail, Long receiverId) {
        try {
            User senderUser = User.dbUser(userRepository.findByEmail(userEmail));
            User receiverUser = User.dbUser(userRepository.getById(receiverId));
            Long senderId = senderUser.getId();
            List<Message> messageList = messageRepository.findByRoomId(senderId + "E" + receiverId);
            if (messageList.isEmpty()) {
                messageList = messageRepository.findByRoomId(receiverId + "E" + senderId);
                if (messageList.isEmpty()) {
                    messageList.add(messageRepository.save(new Message(senderUser.getNickname(), "New Private Chat Room", receiverUser.getNickname(), receiverId + "E" + senderId)));
                }
            }
            return messageList;
        } catch (RuntimeException e) {
            logger.error("Get new/existing private chat room failed");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * adds message to private chat room to the db
     *
     * @param message - the message`s data
     * @return saved message
     */
    public Message addMessageToPrivateChat(Message message) {
        try {
            message.setIssueDate(getLocalDateTimeNow());
            message.setIssueDateEpoch(message.getIssueDate().toEpochSecond(ZoneOffset.of("Z")));
            return messageRepository.save(message);
        } catch (RuntimeException e) {
            logger.error("Add message to private chat failed");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * downloads private room messages
     *
     * @param roomId - the roomId`s data
     * @return list of messages
     */
    public List<Message> downloadPrivateRoomMessages(String roomId) {
        try {
            logger.info("Try to download private chat room messages");
            return messageRepository.findByRoomId(roomId);
        } catch (RuntimeException e) {
            logger.error("Get private room messages to download failed");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * adding message to db
     *
     * @param message - the message`s data
     * @return a saved message body
     */
    public Message addMessageToMainChat(Message message) {
        try {
            logger.info("Try to add message to main chat room");
            String userNickname = message.getSender();
            User user = User.dbUser(userRepository.findByNickname(userNickname));
            if (user.isMute()) {
                throw new IllegalArgumentException("User is Muted");
            }
            message.setIssueDate(getLocalDateTimeNow());
            message.setIssueDateEpoch(message.getIssueDate().toEpochSecond(ZoneOffset.of("Z")));
            message.setReceiver("main");
            return messageRepository.save(message);
        } catch (RuntimeException e) {
            logger.error("Add message to main chat failed");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * find all the main chat room messages in the db
     *
     * @param size - the number of returned rows
     * @return list of messages sorted by DESC timestamp
     */
    public List<Message> getMainRoomMessages(int size) {
        try {
            logger.info("Try to get main chat room messages");
            return messageRepository.findByRoomId("0", PageRequest.of(0, size, Sort.Direction.DESC, "id"));
        } catch (RuntimeException e) {
            logger.error("Get main chat room messages failed");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * find all the main chat room messages in the db from specific time till now
     *
     * @param time - the time in epoch seconds
     * @return list of messages from that time till now
     */
    public List<Message> getMainRoomMessagesByTime(long time) {
        return messageRepository.findByRoomIdAndIssueDateEpochBetween("0", time, getLocalDateTimeNow().toEpochSecond(ZoneOffset.of("Z")));
    }

    public void updateUserEmailMessages(String oldEmail, String newEmail) {
        User user = User.dbUser(userRepository.findByEmail(oldEmail));
        List<Message> senderMessages = messageRepository.findBySender(user.getNickname());
        List<Message> newSenderMessages = senderMessages.stream().filter(message -> message.getSender().equals(oldEmail)).collect(Collectors.toList());
        newSenderMessages.forEach(message -> message.setSender(newEmail));
        newSenderMessages.forEach(message -> messageRepository.save(message));

        List<Message> receiverMessages = messageRepository.findByReceiver(user.getNickname());
        List<Message> newReceiverMessages = receiverMessages.stream().filter(message -> message.getSender().equals(oldEmail)).collect(Collectors.toList());
        newReceiverMessages.forEach(message -> message.setReceiver(newEmail));
        newReceiverMessages.forEach(message -> messageRepository.save(message));
    }

    public void updateUserNicknameMessages(String oldNickname, String newNickname) {
        User user = User.dbUser(userRepository.findByNickname(oldNickname));
        List<Message> senderMessages = messageRepository.findBySender(user.getNickname());
        List<Message> newSenderMessages = senderMessages.stream().filter(message -> message.getSender().equals(oldNickname)).collect(Collectors.toList());
        newSenderMessages.forEach(message -> message.setSender(newNickname));
        newSenderMessages.forEach(message -> messageRepository.save(message));

        List<Message> receiverMessages = messageRepository.findByReceiver(user.getNickname());
        List<Message> newReceiverMessages = receiverMessages.stream().filter(message -> message.getReceiver().equals(oldNickname)).collect(Collectors.toList());
        newReceiverMessages.forEach(message -> message.setReceiver(newNickname));
        newReceiverMessages.forEach(message -> messageRepository.save(message));
    }
}
