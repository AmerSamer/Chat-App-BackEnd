package chatApp.service;

import chatApp.controller.AuthController;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLDataException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    AuthController authController;

    @Autowired
    private UserRepository userRepo;

    User user;

    @BeforeEach
    void newUser(){
        user = new User();
        user.setEmail("abcd123@gmail.com");
        user.setPassword("abcdABCD123");
        user.setName("abcd");
        user.setIssueDate(LocalDate.now());
    }

    @Test
    void createUser_insertUserInDB_saveUserInDB() throws SQLDataException {
        User user = new User();
        user.setEmail("bbb222@gmail.com");
        user.setPassword("bbbBBB222");
        user.setName("bbb");
        User user1 = authService.addUser(user);
        assertEquals(user, userRepo.findByEmail(user1.getEmail()));
        userRepo.delete(user1);
    }
    @Test
    void login_checkPassword_incorrectPasswords() throws SQLDataException {
        User user1 = authService.addUser(user);
        user1.setPassword("aa");
        assertThrows(SQLDataException.class , () ->{authService.login(user);} );
    }

    @Test
    void loginSucceeded() throws SQLDataException {
        User user1 = authService.addUser(user);
        assertEquals(user1, authService.login(user1));
    }

    @Test
    void loginAsGuest_checkName_addPrefixGuest() throws SQLDataException {
        User user = new User();
        user.setName("a");
        User user1 = authService.addGuest(user);
        assertEquals(user.getName(), user1.getName());
        userRepo.delete(user1);
    }

    @Test
    void loginAsGuest_checkNameExistsInRepository() throws SQLDataException {
        User user = new User();
        user.setName("avdc");
        User user1 = authService.addGuest(user);
        assertThrows(SQLDataException.class , () ->{authService.addGuest(user1);} );
        userRepo.delete(user);
    }
    @Test
    void loginAsGuest_Succeeded() throws SQLDataException {
        User user = new User();
        user.setName("avdc");
        assertEquals(userRepo.findByEmail(user.getEmail()), authService.addGuest(user));
        userRepo.delete(user);
    }
    @Test
    void verifyEmail_checkUserExistsInDB_userExistsInDB() {

        assertThrows(SQLDataException.class, () ->{authService.verifyEmail(user);});
    }
    @Test
    void verifyEmail_checkUserIsActivate_userAlreadyActivated() throws SQLDataException {

        authService.addUser(user);
        user.setEnabled(true);
        assertThrows(SQLDataException.class, () ->{authService.verifyEmail(user);}  );
        userRepo.delete(user);
    }
    @Test
    void verifyEmail_checkLocalDate() throws SQLDataException {

        authService.addUser(user);
        user.setIssueDate(user.getIssueDate().plusDays(1));
        assertThrows(SQLDataException.class, () ->{authService.verifyEmail(user);}  );
        userRepo.delete(user);
    }
    @Test
    void verifyEmail_verifyCode() throws SQLDataException {

        User user1 = authService.addUser(user);
        user.setVerifyCode(user1.getVerifyCode() + "aa");
        assertThrows(SQLDataException.class, () ->{authService.verifyEmail(user);}  );
        userRepo.delete(user);
    }
    @Test
    void verifyEmail_Succeeded() throws SQLDataException {

        User user1 = authService.addUser(user);
        User user2 = authService.verifyEmail(user1);
        assertTrue(user2.isEnabled());
        assertNull(user2.getVerifyCode());
        assertEquals(userRepo.findByEmail(user.getEmail()), user2);
        userRepo.delete(user);
    }

//    @Test
//    void login_existingUser_updateOnlineStatus() throws SQLDataException {
//        User user1 = authService.login(user);
//        assertEquals(UserStatuses.ONLINE, user1.getUserStatus());
//    }
//    @Test
//    void login_existingUser_emailNotExist() {
//        user.setEmail("aaa111@gmail.com");
//        assertThrows(SQLDataException.class, () ->{authService.login(user);}  );
//    }
//
//    @Test
//    void loginAsGuest_checkEmail_addEmailGuest() throws SQLDataException {
//        User user = new User();
//        user.setName("b");
//        User user1 = authService.addGuest(user);
//        assertEquals(user.getEmail() ,user1.getEmail());
//        userRepo.delete(user1);
//    }
}