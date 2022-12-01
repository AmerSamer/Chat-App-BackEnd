package chatApp.service;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static chatApp.utilities.ExceptionMessages.updateUserFailedMessage;
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
    User user2;
    @BeforeEach
    void newUser() {
        this.user = User.createUser("a", "a11222222@gmail.com", "aA12345");
        authService.addUser(user);
        user.setPassword("aA12345");
        authService.login(user);
        this.user2 = User.createUser("a1", "aa11222222@gmail.com", "aA12345");
        authService.addUser(user2);
        user2.setPassword("aA12345");
//        authService.login(user);
    }

    @AfterEach
    void deleteUser() {
        userRepository.delete(user);
        userRepository.delete(user2);
    }

    @Test
    void logoutUser_checkLogoutGuestUser_changeStatusToOffline() {
        user.setUserStatus(UserStatuses.OFFLINE);
        userRepository.save(user);
        user.setPassword("aA12345");
        assertEquals(UserStatuses.OFFLINE, userService.logoutUser(authService.getKeyEmailsValTokens().get(user.getEmail())).getUserStatus());
    }

    @Test
    void logoutUser_checkLogoutRegisteredUser_deleteToken() {
        authService.verifyEmail(user);
        userService.logoutUser(authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertNull(authService.getKeyEmailsValTokens().get(user.getEmail()));
    }

    @Test
    void updateUser_updateUserName_successfulUpdate() {
        user.setName("tteesstt");
        User u = userService.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(user.getName(), u.getName());
    }

    @Test
    void updateUser_updateUserName_failedUpdate() {
        user.setName("@");
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        });
    }
    @Test
    void updateUser_updateUserDateOfBirth_failedUpdate() {
        user.setDateOfBirth(LocalDate.of(2023,4,20));
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        });
    }
    @Test
    void updateUser_updateUserDateOfBirth_successfulUpdate() {
        user.setDateOfBirth(LocalDate.of(1995,4,20));
        user.setAge(LocalDate.now().minusYears(user.getDateOfBirth().getYear()).getYear());
        User u = userService.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(user.getAge(),u.getAge());
    }
    @Test
    void updateUser_updateUserDescription_successfulUpdate() {
        user.setDescription("king of the kings");
        User u = userService.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(user.getDescription(),u.getDescription());
    }
    @Test
    void updateUser_updateUserPhoto_successfulUpdate() {
        user.setPhoto("https://www.realmadrid.com/StaticFiles/RealMadridResponsive/images/static/og-image.png");
        User u = userService.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(user.getPhoto(),u.getPhoto());
    }
    @Test
    void updateStatusUser_updateStatusUserOnline_successfulUpdate() {
        User u = userService.updateStatusUser(authService.getKeyEmailsValTokens().get(user.getEmail()), "online");
        assertEquals(UserStatuses.ONLINE,u.getUserStatus());
    }
    @Test
    void updateStatusUser_updateStatusUserAway_successfulUpdate() {
        User u = userService.updateStatusUser(authService.getKeyEmailsValTokens().get(user.getEmail()), "away");
        assertEquals(UserStatuses.AWAY,u.getUserStatus());
    }
    @Test
    void updateStatusUser_updateStatusUserEmailNull_throwNewIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateStatusUser(null, "away");
        });
    }
    @Test
    void getAllUsers_getAllUsersNotOffline_listOfUsers() {
        List<User> l = userService.getAllUsers();
        List<User> newl = new ArrayList<>();
        newl.add(user);
        assertEquals(newl.get(0).getId(),l.get(0).getId());
    }
    @Test
    void updateMuteUnMuteUser_updateMuteUserToUnMute_successfulUpdate() {
        user.setType(UserType.ADMIN);
        userRepository.save(user);
        User u = userService.updateMuteUnmuteUser(user2.getId(), authService.getKeyEmailsValTokens().get(user.getEmail()));
        assertEquals(!user2.isMute(),u.isMute());
    }
    @Test
    void updateMuteUnMuteUser_updateMuteUserToUnMuteNotAdmin_failedUpdate() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateMuteUnmuteUser(user2.getId(), authService.getKeyEmailsValTokens().get(user.getEmail()));
        });
    }
    @Test
    void updateMuteUnMuteUser_updateMuteUserToUnMuteNullEmail_failedUpdate() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateMuteUnmuteUser(user2.getId(), null);
        });
    }
    @Test
    void updateMuteUnMuteUser_updateMuteUserToUnMuteNullId_failedUpdate() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateMuteUnmuteUser(80000000000000000L, authService.getKeyEmailsValTokens().get(user.getEmail()));
        });
    }
}