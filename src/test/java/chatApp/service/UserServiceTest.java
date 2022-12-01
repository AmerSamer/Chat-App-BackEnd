package chatApp.service;

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
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;
    User user;

    @BeforeEach
    void newUser() {
        this.user = User.createUser("a", "a11222222@gmail.com", "aA12345");
        authService.addUser(user);
        user.setPassword("aA12345");
        authService.login(user);
    }

    @AfterEach
    void deleteUser() {
        userRepository.delete(user);
    }

    @Test
    void logoutUser_checkLogoutGuestUser_changeStatusToOffline() {
        user.setUserStatus(UserStatuses.OFFLINE);
        userRepository.save(user);
        user.setPassword("aA12345");
        assertEquals(UserStatuses.OFFLINE , userService.logoutUser(authService.getKeyEmailsValTokens().get(user.getEmail())).getUserStatus());
    }

    @Test
    void logoutUser_checkLogoutRegisteredUser_deleteToken(){
        authService.verifyEmail(user);
        userService.logoutUser(authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertNull(authService.getKeyEmailsValTokens().get(user.getEmail()));
    }
//    @Test
//    void updateUser() {
//    }
//
//    @Test
//    void logoutUser() {
//    }
//
//    @Test
//    void getAllUsers() {
//    }
//
//    @Test
//    void updateMuteUnmuteUser() {
//    }
//
//    @Test
//    void updateStatusUser() {
//    }
}