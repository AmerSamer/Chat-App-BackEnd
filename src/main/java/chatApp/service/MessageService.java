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

import static chatApp.utilities.ExceptionMessages.*;
import static chatApp.utilities.Utility.*;

@CrossOrigin
@Service
public class MessageService {

    private static final Logger logger = LogManager.getLogger(MessageService.class.getName());

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
     * @throws IllegalArgumentException
     */
    public List<Message> getPrivateRoomMessages(String userEmail, Long receiverId) {
        try {
            logger.info("Try to get private room messages between 2 users");
            User senderUser = User.dbUser(userRepository.findByEmail(userEmail));
            User receiverUser = User.dbUser(userRepository.getById(receiverId));
            Long senderId = senderUser.getId();
            logger.info("Try to get private room messages between " + senderUser.getNickname() + " and " + receiverUser.getNickname());
            logger.info("check if the room " + senderId + "E" + receiverId + " exists");
            List<Message> messageList = messageRepository.findByRoomId(senderId + "E" + receiverId);
            if (messageList.isEmpty()) {
                logger.info("check if the room " + receiverId + "E" + senderId + " exists");
                messageList = messageRepository.findByRoomId(receiverId + "E" + senderId);
                if (messageList.isEmpty()) {
                    logger.info("creating new room " + senderId + "E" + receiverId + " exists");
                    messageList.add(messageRepository.save(new Message(senderUser.getNickname(), "New Private Chat Room", receiverUser.getNickname(), senderId + "E" + receiverId)));
                }
            }
            return messageList;
        } catch (RuntimeException e) {
            logger.error(privateChatRoomMessagesFailed);
            throw new IllegalArgumentException(privateChatRoomMessagesFailed);
        }
    }

    /**
     * adds message to private chat room to the db
     *
     * @param message - the message`s data
     * @return saved message
     * @throws IllegalArgumentException
     */
    public Message addMessageToPrivateChat(Message message) {
        try {
            logger.info("Try to add a message to a private chat room id: " + message.getRoomId());
            message.setIssueDate(getLocalDateTimeNow());
            message.setIssueDateEpoch(message.getIssueDate().toEpochSecond(ZoneOffset.of("Z")));
            return messageRepository.save(message);
        } catch (RuntimeException e) {
            logger.error(FailedToSendPrivateMessage);
            throw new IllegalArgumentException(FailedToSendPrivateMessage);
        }
    }

    /**
     * downloads private room messages
     *
     * @param roomId - the roomId`s data
     * @return list of messages
     * @throws IllegalArgumentException
     */
    public List<Message> downloadPrivateRoomMessages(String roomId) {
        try {
            logger.info("Try to download private chat room messages");
            return messageRepository.findByRoomId(roomId);
        } catch (RuntimeException e) {
            logger.error(downloadPrivateRoomFailed);
            throw new IllegalArgumentException(downloadPrivateRoomFailed);
        }
    }

    /**
     * adding message to db
     *
     * @param message - the message`s data
     * @return a saved message body
     * @throws IllegalArgumentException
     */
    public Message addMessageToMainChat(Message message) {
        try {
            logger.info("Try to add message to main chat room");
            String userNickname = message.getSender();
            User user = User.dbUser(userRepository.findByNickname(userNickname));
            if (user.isMute()) {
                throw new IllegalArgumentException(userIsMutedMessage);
            }
            message.setIssueDate(getLocalDateTimeNow());
            message.setIssueDateEpoch(message.getIssueDate().toEpochSecond(ZoneOffset.of(zoneOffsetId)));
            message.setReceiver(mainRoomReceiverName);
            return messageRepository.save(message);
        } catch (RuntimeException e) {
            logger.error("Add message to main chat failed");
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * find all the main chat room messages in the db
     *
     * @param size - the number of returned rows
     * @return list of messages sorted by DESC timestamp
     * @throws IllegalArgumentException
     */
    public List<Message> getMainRoomMessages(int size) {
        try {
            logger.info("Try to get main chat room messages");
            return messageRepository.findByRoomId(mainRoomId, PageRequest.of(0, size, Sort.Direction.DESC, "id"));
        } catch (RuntimeException e) {
            logger.error(mainChatRoomMessagesFailed);
            throw new IllegalArgumentException(mainChatRoomMessagesFailed);
        }
    }

    /**
     * find all the main chat room messages in the db from specific time till now
     *
     * @param time - the time in epoch seconds
     * @return list of messages from that time till now
     */
    public List<Message> getMainRoomMessagesByTime(long time) {
        return messageRepository.findByRoomIdAndIssueDateEpochBetween(mainRoomId, time, getLocalDateTimeNow().toEpochSecond(ZoneOffset.of(zoneOffsetId)));
    }
}
