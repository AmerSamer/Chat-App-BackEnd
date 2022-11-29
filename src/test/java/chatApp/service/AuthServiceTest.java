package chatApp.service;

import chatApp.controller.AuthController;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLDataException;

import static org.junit.jupiter.api.Assertions.*;

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
    void login_existingUser_updateOnlineStatus() throws SQLDataException {
        User user1 = authService.login(user);
        assertEquals(UserStatuses.ONLINE, user1.getUserStatus());
    }
    @Test
    void login_existingUser_emailNotExist() {
        user.setEmail("aaa111@gmail.com");
        assertThrows(SQLDataException.class, () ->{authService.login(user);}  );
    }

    @Test
    void verifyEmail_checkUserExistsInDB_userExistsInDB() {

        assertThrows(SQLDataException.class, () ->{authService.verifyEmail(user);}  );
    }

    @Test
    void verifyEmail_checkUserIsActivate_userAlreadyActivated() {

        user.setEnabled(true);
        assertThrows(SQLDataException.class, () ->{authService.verifyEmail(user);}  );
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
    void loginAsGuest_checkEmail_addEmailGuest() throws SQLDataException {
        User user = new User();
        user.setName("b");
        User user1 = authService.addGuest(user);
        assertEquals(user.getEmail() ,user1.getEmail());
        userRepo.delete(user1);
    }

    @Test
    void login() {
    }

    @Test
    void addGuest() {
    }

    @Test
    void addUser() {
    }

    @Test
    void verifyEmail() {
    }

    @Test
    void sendMessage() {
    }

    @Test
    void getTokensInstance() {
    }

    @Test
    void getEmailsInstance() {
    }

    @Test
    void getKeyTokensValEmails() {
    }

    @Test
    void getKeyEmailsValTokens() {
    }
}