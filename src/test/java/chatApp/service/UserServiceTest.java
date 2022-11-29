package chatApp.service;

import chatApp.controller.UserController;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLDataException;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    void newUser() throws SQLDataException {
        this.user = new User();
        user.setEmail("a11222222@gmail.com");
        user.setPassword("aA11");
        user.setName("a");
        authService.addUser(user);
        authService.login(user);
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