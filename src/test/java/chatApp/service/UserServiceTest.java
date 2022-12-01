package chatApp.service;

import chatApp.controller.UserController;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLDataException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

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
        this.user = User.registerUser("test", "test@gmail.com", "Sse12345");
        authService.addUser(this.user);
        authService.login(this.user);
//        this.user1 = User.registerUser("testt", "testt@gmail.com", "Sse12345");
//        authService.addUser(this.user1);
    }

    @AfterEach
    void deleteUser() {
        userRepository.delete(user);
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
    void updateUser() {
        user.setName("testupdated");
        User returnedUser = userService.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(user.getName(), returnedUser.getName());
    }

    @Test
    void logoutUser() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void updateMuteUnmuteUser() {
    }

    @Test
    void updateStatusUser() {
    }
}