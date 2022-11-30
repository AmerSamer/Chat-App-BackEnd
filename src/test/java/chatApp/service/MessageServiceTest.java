package chatApp.service;

import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLDataException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
class MessageServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    Message mainMessage;
    Message privateMessage;
    User userSender;
    User userReceiver;

    @BeforeEach
    void newMessage() throws SQLDataException {
        this.userSender = User.registerUser("shai", "samerelishai@gmail.com", "Aa12345");
        authService.addUser(this.userSender);
        authService.login(this.userSender);
        this.userReceiver = User.registerUser("elisamer", "seselevtion@gmail.com", "Aa12345");
        authService.addUser(this.userReceiver);
        this.mainMessage = new Message("samerelishai@gmail.com", "hello main content", "main", "0");
        messageService.addMessageToMainChat(mainMessage);
        this.privateMessage = new Message("samerelishai@gmail.com", "hello elisamer content", "seselevtion@gmail.com", userSender.getId() + "E" + userReceiver.getId());
        messageService.addMessageToPrivateChat(privateMessage);
    }


    @AfterEach
    public void deleteAllTables(){
        userRepository.deleteAll();
        messageRepository.deleteAll();
    }

    @Test
    void getPrivateRoomMessages_checkRoomIdMessagesAreFromTheSameRoom_true() {
        List<Message> messages = messageService.getPrivateRoomMessages(userSender.getEmail(), userReceiver.getId());
        assertTrue(messages.stream().allMatch(message -> Objects.equals(message.getRoomId(), privateMessage.getRoomId())));
    }


    @Test
    void getPrivateRoomMessages_roomIdExistsInTheOppositeWay_NotEquals() {
        List<Message> messages = messageService.getPrivateRoomMessages(userReceiver.getEmail(), userSender.getId());
        assertNotEquals(messages, messageRepository.findByRoomId(userReceiver.getId() + "E" + userSender.getId()));
    }

    @Test
    void getPrivateRoomMessages_roomIdDosentExits_equals() {
        User newUser = User.registerUser("new", "new123@gmail.com", "Aa12345");
        User dbuser = User.dbUser(authService.addUser(newUser));
        List<Message> messages = messageService.getPrivateRoomMessages(userReceiver.getEmail(), dbuser.getId());
        assertEquals(messages.get(0).getContent(), "New Private Chat Room");
    }

    @Test
    void addMessageToPrivateChat_roomIdEqualsToRetrievedMessageRoomId_equals() {
        privateMessage.setContent("check");
        Message message = messageService.addMessageToPrivateChat(privateMessage);
        assertEquals(message.getRoomId(), messageRepository.findByContent("check").get(0).getRoomId());
    }

    @Test
    void downloadPrivateRoomMessages_checkIfRoomIdExists_notNull() {
        assertNotNull(messageService.downloadPrivateRoomMessages(privateMessage.getRoomId()));
    }

    @Test
    void addMessageToMainChat_checkIfTheReceiverIsMainChat_equals(){
        Message newMessage = new Message("samerelishai@gmail.com", "hello main content", null, "0");
        Message message = messageService.addMessageToMainChat(newMessage);
        assertEquals(message.getReceiver(), "main");
    }

    @Test
    void addMessageToMainChat_userIsMuted_throwsIllegealArgumentException(){
        userSender.setMute(true);
        userRepository.save(userSender);
        Message newMessage = new Message("samerelishai@gmail.com", "hello main content", null, "0");
        assertThrows(IllegalArgumentException.class, () ->{messageService.addMessageToMainChat(newMessage);} );
    }


    @Test
    void getMainRoomMessages() {
        List<Message> messages = messageService.getMainRoomMessages(1);
        assertEquals(messages.size(), 1);
    }

    @Test
    void getMainRoomMessagesByTime(){
        List<Message> messages = messageService.getMainRoomMessagesByTime(mainMessage.getIssueDateEpoch() - 1);
        assertEquals(0, messages.stream().filter(msg -> !Objects.equals(msg.getRoomId(), "0")).count());
    }
}