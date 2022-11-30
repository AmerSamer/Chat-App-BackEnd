package chatApp.service;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static chatApp.utilities.Utility.randomString;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    private UserRepository userRepo;

    User user;

    @BeforeEach
    void newUser(){
        this.user = User.registerUser("abcd", "abcd1234567@gmail.com", "abcdABCD123");
    }

    @AfterEach
    void delete(){ userRepo.delete(user);}

    @Test
    void addUser_insertUserInDB_saveUserInDB()  {
        assertEquals(user, userRepo.findByEmail(authService.addUser(user).getEmail()));
    }
    @Test
    void login_insertUserInDB_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->{authService.login(user);}  );
    }
    @Test
    void login_checkStatus_statusOnline()  {
        authService.addUser(user);
        assertNotEquals(UserStatuses.OFFLINE, userRepo.findByEmail(authService.login(user).getUserStatus().toString()));
    }
    @Test
    void addUser_checkEmailExists_IllegalArgumentException()  {
        User user1 = user.registerUser("abcdCopy", "abcd1234567@gmail.com", "abcdABCD123Copy");
        authService.addUser(user);
        assertThrows(IllegalArgumentException.class, () ->{authService.addUser(user);} );
        userRepo.delete(user1);
    }
    @Test
    void addGuest_checkNameExists_IllegalArgumentException()  {
        User user1 = user.registerUser("abcd", "abcd123@copygmail.com", "abcdABCD123Copy");
        authService.addGuest(user1);
        assertThrows(IllegalArgumentException.class, () ->{authService.addGuest(user);} );
        userRepo.delete(user1);

    }
    @Test
    void verifyEmail_checkUerExists_IllegalArgumentException()  {
        assertThrows(IllegalArgumentException.class,()->{authService.verifyEmail(user);});
    }
    @Test
    void verifyEmail_checkEnabled_IllegalArgumentException1()  {
        user.setEnabled(true);
        assertThrows(IllegalArgumentException.class,()->{ authService.verifyEmail(authService.addUser(user));});
    }
    @Test
    void verifyEmail_checkIfPassDay_IllegalArgumentException()  {

        User user1 = user.registerUser("abcde", "abc486@comail.com", "abcdABCD1234");
        authService.addUser(user1);
        user1.setIssueDate(LocalDate.now().minusDays(5));
        userRepo.save(user1);
        assertThrows(IllegalArgumentException.class, () ->{authService.verifyEmail(user1);}  );
        userRepo.delete(user1);
    }
    @Test
    void verifyEmail_checkVerifyCode_IllegalArgumentException() {
        User user1 = authService.addUser(user);
        user.setVerifyCode(user1.getVerifyCode() + "aa");
       // user.setIssueDate(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () ->{authService.verifyEmail(user);}  );
        userRepo.delete(user1);
    }
    @Test
    void verifyEmail_checkType_registeredType()  {
        user.setVerifyCode(randomString());
        user.setIssueDate(LocalDate.now());
        assertEquals(UserType.REGISTERED, authService.verifyEmail(authService.addUser(user)).getType());
    }


//    @Test
//    void loginAsGuest_checkName_addPrefixGuest() throws SQLDataException {
//        User user = new User();
//        user.setName("a");
//        User user1 = authService.addGuest(user);
//        assertEquals(user.getName(), user1.getName());
//        userRepo.delete(user1);
//
//
//    }
//    @Test
//    void login_checkPassword_incorrectPasswords() throws SQLDataException {
//        User user1 = authService.addUser(user);
//        user1.setPassword("aa");
//        assertThrows(SQLDataException.class , () ->{authService.login(user);} );
//    }
//
//    @Test
//    void loginSucceeded() throws SQLDataException {
//        User user1 = authService.addUser(user);
//        assertEquals(user1, authService.login(user1));
//    }
//
//
//    @Test
//    void loginAsGuest_checkNameExistsInRepository() throws SQLDataException {
//        User user = new User();
//        user.setName("avdc");
//        User user1 = authService.addGuest(user);
//        assertThrows(SQLDataException.class , () ->{authService.addGuest(user1);} );
//        userRepo.delete(user);
//    }
//    @Test
//    void loginAsGuest_Succeeded() throws SQLDataException {
//        User user = new User();
//        user.setName("avdc");
//        assertEquals(userRepo.findByEmail(user.getEmail()), authService.addGuest(user));
//        userRepo.delete(user);
//    }
//    @Test
//    void verifyEmail_checkUserExistsInDB_userExistsInDB() {
//
//        assertThrows(SQLDataException.class, () ->{authService.verifyEmail(user);});
//    }
//    @Test
//    void verifyEmail_checkUserIsActivate_userAlreadyActivated() throws SQLDataException {
//
//        authService.addUser(user);
//        user.setEnabled(true);
//        assertThrows(SQLDataException.class, () ->{authService.verifyEmail(user);}  );
//        userRepo.delete(user);
//    }
//    @Test
//    void verifyEmail_checkLocalDate() throws SQLDataException {
//
//        authService.addUser(user);
//        user.setIssueDate(user.getIssueDate().plusDays(1));
//        assertThrows(SQLDataException.class, () ->{authService.verifyEmail(user);}  );
//        userRepo.delete(user);
//    }
//    @Test
//    void verifyEmail_verifyCode() throws SQLDataException {
//
//        User user1 = authService.addUser(user);
//        user.setVerifyCode(user1.getVerifyCode() + "aa");
//        assertThrows(SQLDataException.class, () ->{authService.verifyEmail(user);}  );
//        userRepo.delete(user);
//    }
//    @Test
//    void verifyEmail_Succeeded() throws SQLDataException {
//
//        User user1 = authService.addUser(user);
//        User user2 = authService.verifyEmail(user1);
//        assertTrue(user2.isEnabled());
//        assertNull(user2.getVerifyCode());
//        assertEquals(userRepo.findByEmail(user.getEmail()), user2);
//        userRepo.delete(user);
//    }
//=====================================================================================
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