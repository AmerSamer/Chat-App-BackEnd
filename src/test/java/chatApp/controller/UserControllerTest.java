package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
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

import static chatApp.Utilities.ExceptionMessages.invalidEmailMessage;
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

    @BeforeEach
    void newUser() throws SQLDataException {
        this.user = User.registerUser("a", "a11222222@gmail.com", "aA11");
        authService.addUser(this.user);
        authService.login(this.user);
    }

    @AfterEach
    void deleteUser() {
        userRepository.delete(user);
    }

    @Test
    void updateUser_updateName_newName(){
        user.setName("ses");
        ResponseEntity<CustomResponse<UserDTO>> user1 = userController.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(user.getName(),user1.getBody().getResponse().getName());
    }
    @Test
    void updateUser_updateInvalidEmail_invalidMessage(){
        user.setEmail("ses");
        ResponseEntity<CustomResponse<UserDTO>> user1 = userController.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(invalidEmailMessage ,user1.getBody().getMessage());
    }


    @Test
    void logoutUser_checkLogoutGuestUser_changeStatusToOffline() throws SQLDataException {

        assertEquals(UserStatuses.OFFLINE ,userService.logoutUser(authService.getKeyEmailsValTokens().get(user.getEmail())).getUserStatus());
    }

    @Test
    void logoutUser_checkLogoutRegisteredUser_deleteToken() throws SQLDataException {

        authService.verifyEmail(user);
        userService.logoutUser(authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertNull(authService.getKeyEmailsValTokens().get(user.getEmail()));
    }

    @Test
    void updateMuteUser() {

    }

    @Test
    void updateStatusUser() {
    }
}