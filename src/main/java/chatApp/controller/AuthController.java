package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;

import static chatApp.Utilities.ExceptionMessages.*;
import static chatApp.Utilities.SuccessMessages.*;
import static chatApp.Utilities.Utility.*;

@RestController
@CrossOrigin
@RequestMapping("/sign")
public class AuthController {

    private static Logger logger = LogManager.getLogger(AuthController.class.getName());
    @Autowired
    private AuthService authService;

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> createUser(@RequestBody User user) {
        try {
            if (!isValidEmail(user.getEmail())) {
                logger.error(invalidEmailMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidEmailMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (!isValidName(user.getName())) {
                logger.error(invalidNameMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidNameMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (!isValidPassword(user.getPassword())) {
                logger.error(invalidPasswordMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidPasswordMessage);
                return ResponseEntity.badRequest().body(response);
            }
            logger.info("Try to register " + user.getEmail() + " to the system");
            User createUser = authService.addUser(user);
            UserDTO userDTO = UserDTO.userToUserDTO(createUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, registrationSuccessfulMessage);
            logger.info(registrationSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (SQLDataException e) {
            logger.error(emailExistsInSystemMessage(user.getEmail()));
            CustomResponse<UserDTO> response = new CustomResponse<>(null, emailExistsInSystemMessage(user.getEmail()));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> login(@RequestBody User user) {
        try {
            if (!isValidEmail(user.getEmail())) {
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidEmailMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (!isValidPassword(user.getPassword())) {
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidPasswordMessage);
                return ResponseEntity.badRequest().body(response);
            }
            logger.info("Try to login : " + user.getEmail() + " to the system");
            User logUser = authService.login(user);
            String header = authService.getKeyEmailsValTokens().get(user.getEmail());
            UserDTO userDTO = UserDTO.userToUserDTO(logUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, loginSuccessfulMessage, header);
            logger.info(loginSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (SQLDataException e) {
            logger.error(loginFailedMessage);
            CustomResponse<UserDTO> response = new CustomResponse<>(null, loginFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(value = "login/guest", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> loginAsGuest(@RequestBody User user) {
        try {
            if (!isValidName(user.getName())) {
                logger.error(invalidNameMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidNameMessage);
                return ResponseEntity.badRequest().body(response);
            }
            logger.info("Try to login as guest to the system");
            User userGuest = authService.addGuest(user);
            String header = authService.getKeyEmailsValTokens().get(user.getEmail());
            UserDTO userDTO = UserDTO.userGuestToUserDTO(userGuest);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, loginSuccessfulMessage, header);
            logger.info(loginSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (SQLDataException e) {
            logger.error(loginAsGuestFailedMessage);
            CustomResponse<UserDTO> response = new CustomResponse<>(null, loginAsGuestFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(value = "activate", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> verifyEmail(@RequestBody User user) {
        try {
            User userVerify = authService.verifyEmail(user);
            UserDTO userDTO = UserDTO.userToUserDTO(userVerify);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, activationEmailSuccessfulMessage);
            logger.info(activationEmailSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (SQLDataException e) {
            logger.error(activationEmailFailedMessage);
            CustomResponse<UserDTO> response = new CustomResponse<>(null, activationEmailFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
