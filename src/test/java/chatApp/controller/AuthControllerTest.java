package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLDataException;

import static chatApp.Utilities.Utility.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthControllerTest {

    @Autowired
    AuthService authService;

    @Autowired
    AuthController authController;

    @Autowired
    private UserRepository userRepo;

//    @Autowired
//    private TestEntityManager testEntityManager;

//    @AfterEach
//    @BeforeEach
//    public void deleteAllTables(){
//        userRepo.deleteAll();
//    }

    User user;

    @BeforeEach
    void newUser(){
        user = new User();
        user.setEmail("abcd123@gmail.com");
        user.setPassword("abcdABCD123");
        user.setName("abcd");
    }


    @Test
    void createUser_validateUserInput_isValid() {
        assertTrue(isValidEmail(user.getEmail()) && isValidName(user.getName())
                && isValidPassword(user.getPassword()));
    }

    @Test
    void createUser_validateEmailInput_emailNotValid() {
        user.setEmail("shaigmail.com");
        assertFalse(isValidEmail(user.getEmail()));
    }

    @Test
    void createUser_validatePasswordInput_passwordNotValid() {
        user.setPassword("shai1234");
        assertFalse(isValidEmail(user.getPassword()));
    }

    @Test
    void createUser_validateNameInput_nameNotValid() {
        user.setName("shai1234");
        assertFalse(isValidEmail(user.getName()));
    }

    @Test
    void createUser_validateEmailInput_emailIsNull() {
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    void createUser_validatePasswordInput_passwordIsNull() {
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    void createUser_validateNameInput_nameIsNull() {
        user.setName(null);
        assertNull(user.getName());
    }

//    @Test
//    void createUser_registerUserFlow_isValid() throws SQLDataException {
//        User savedUser = userRepo.save(user);
//
//        User existUser = testEntityManager.find(User.class, savedUser.getId());
//        assertNotNull(existUser);
//    }

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
    void login_existingUser_tokenNotNull() {
        ResponseEntity<CustomResponse<UserDTO>> user1 = authController.login(user);
        assertNotNull(user1.getBody().getHeaders());
    }
    @Test
    void login_existingUser_emailNotExist() {
        user.setEmail("aaa111@gmail.com");
        assertThrows(SQLDataException.class, () ->{authService.login(user);}  );
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
    void loginAsGuest() {
    }

    @Test
    void verifyEmail() {

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
}