package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
import chatApp.service.MessageService;
import chatApp.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLDataException;

import static chatApp.Utilities.ExceptionMessages.invalidEmailMessage;
import static chatApp.Utilities.ExceptionMessages.userIsMutedMessage;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChatControllerTest {
    @Autowired
    private ChatController chatController;
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
        this.privateMessage = new Message("samerelishai@gmail.com", "hello elisamer content", "seselevtion@gmail.com", "1E2");
    }

    @AfterEach
    public void deleteAllTables(){
        userRepository.deleteAll();
    }

    @Test
    void sendMainPlainMessage_contentInDatabaseEqualsContentFromClient_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendMainPlainMessage(mainMessage);
        assertEquals(responseMessage.getBody().getResponse().getContent(), mainMessage.getContent());
    }

    @Test
    void sendMainPlainMessage_senderInDatabaseEqualsSenderFromClient_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendMainPlainMessage(mainMessage);
        assertEquals(responseMessage.getBody().getResponse().getSender(), mainMessage.getSender());
    }

    @Test
    void sendMainPlainMessage_roomIdIsZero_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendMainPlainMessage(mainMessage);
        assertEquals(responseMessage.getBody().getResponse().getRoomId(), "0");
    }

    @Test
    void sendMainPlainMessage_userIsMuted_badRequest() {
        userSender.setMute(true);
        userRepository.save(userSender);
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendMainPlainMessage(mainMessage);
        assertEquals(userIsMutedMessage , responseMessage.getBody().getMessage());
    }

    @Test
    void sendPrivatePlainMessage_contentInDatabaseEqualsContentFromClient_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendPrivatePlainMessage(privateMessage);
        assertEquals(responseMessage.getBody().getResponse().getContent(), privateMessage.getContent());
    }

    @Test
    void sendPrivatePlainMessage_senderInDatabaseEqualsSenderFromClient_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendPrivatePlainMessage(privateMessage);
        assertEquals(responseMessage.getBody().getResponse().getSender(), privateMessage.getSender());
    }

    @Test
    void sendPrivatePlainMessage_receiverInDatabaseEqualsReceiverFromClient_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendPrivatePlainMessage(privateMessage);
        assertEquals(responseMessage.getBody().getResponse().getReceiver(), privateMessage.getReceiver());
    }


    @Test
    void getAllUsers() {
    }


    @Test
    void getPrivateRoom() {
    }

    @Test
    void getMainRoom() {
    }

    @Test
    void downloadPrivateRoom() {
    }

    @Test
    void downloadMainRoom() {
    }
}