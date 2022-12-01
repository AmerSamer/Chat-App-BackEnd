package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
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

import static chatApp.utilities.ExceptionMessages.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    UserController userController;

    @Autowired
    UserRepository userRepository;
    User user;
    User user1;

    @BeforeEach
    void newUser() throws SQLDataException {
        this.user = User.createUser("test", "test@gmail.com", "aA12345");
        authService.addUser(this.user);
        this.user.setPassword("aA12345");
        authService.login(this.user);
        this.user1 = User.createUser("testt", "testt@gmail.com", "aA12345");
        authService.addUser(this.user1);
    }

    @AfterEach
    void deleteUser() {
        userRepository.delete(user);
        userRepository.delete(user1);
    }

    @Test
    void updateUser_updateName_newName() {
        user.setName("ses");
        ResponseEntity<CustomResponse<UserDTO>> user1 = userController.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(user.getName(), user1.getBody().getResponse().getName());
    }

    @Test
    void updateUser_updateInvalidEmail_invalidMessage() {
        user.setEmail("ses");
        ResponseEntity<CustomResponse<UserDTO>> user1 = userController.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(invalidEmailMessage, user1.getBody().getMessage());
    }

    @Test
    void updateUser_updatePassword_invalidPasswordMessage() {
        user.setPassword("s");
        ResponseEntity<CustomResponse<UserDTO>> user1 = userController.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(invalidPasswordMessage, user1.getBody().getMessage());
    }

    @Test
    void updateUser_updateName_invalidPasswordMessage() {
        user.setName("@");
        ResponseEntity<CustomResponse<UserDTO>> user1 = userController.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(invalidNameMessage, user1.getBody().getMessage());
    }

    @Test
    void updateUser_update_failedCatch() {
        ResponseEntity<CustomResponse<UserDTO>> userTestRes = userController.updateUser(user, null);
        assertEquals(updateUserFailedMessage, userTestRes.getBody().getMessage());
    }

    @Test
    void updateMuteUser_updateMute_successfulUpdate() {
        user.setType(UserType.ADMIN);
        userRepository.save(user);
        ResponseEntity<CustomResponse<UserDTO>> userTestRes = userController.updateMuteUser(authService.getKeyEmailsValTokens().get(user.getEmail()), user.getId());
        assertEquals(!user.isMute(), userTestRes.getBody().getResponse().isMute());
    }

    @Test
    void updateMuteUser_updateMute_failedCatch() {
        ResponseEntity<CustomResponse<UserDTO>> userTestRes = userController.updateMuteUser(authService.getKeyEmailsValTokens().get(user.getEmail()), user1.getId());
        assertEquals(muteUserFailedMessage, userTestRes.getBody().getMessage());
    }

    @Test
    void updateStatusUser_updateStatus_successfulUpdate() {
        ResponseEntity<CustomResponse<UserDTO>> userTestRes = userController.updateStatusUser(authService.getKeyEmailsValTokens().get(user.getEmail()), "online");
        assertEquals(UserStatuses.ONLINE, userTestRes.getBody().getResponse().getUserStatus());
    }

    @Test
    void updateStatusUser_updateStatus_failedCatch() {
        ResponseEntity<CustomResponse<UserDTO>> userTestRes = userController.updateStatusUser(null, "online");
        assertEquals(updateStatusUserFailedMessage, userTestRes.getBody().getMessage());
    }
    @Test
    void logoutUser_logout_successLogout() {
        ResponseEntity<CustomResponse<UserDTO>> userTestRes = userController.logoutUser(authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(UserStatuses.OFFLINE, userTestRes.getBody().getResponse().getUserStatus());
    }
    @Test
    void logoutUser_logout_FailedLogout() {
        ResponseEntity<CustomResponse<UserDTO>> userTestRes = userController.logoutUser(null);
        assertEquals(logoutUserFailedMessage, userTestRes.getBody().getMessage());
    }
}