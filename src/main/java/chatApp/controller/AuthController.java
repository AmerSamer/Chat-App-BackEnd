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

import java.util.Optional;

import static chatApp.utilities.SuccessMessages.*;
import static chatApp.utilities.Utility.*;

@RestController
@CrossOrigin
@RequestMapping("/sign")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());
    @Autowired
    private AuthService authService;

    /**
     * checks if the email, name, password is valid, and send the user to the addUser method in AuthService
     *
     * @param user - the user's data
     * @return a saved user with response body
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> registerUser(@RequestBody User user) {
        Optional<CustomResponse<UserDTO>> isValid;
        CustomResponse<UserDTO> response = new CustomResponse<>(null, emptyString);
        try {
            isValid = checkValidEmail(user.getEmail(), response);
            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
            isValid = checkValidName(user.getName(), response);
            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
            isValid = checkValidPassword(user.getPassword(), response);
            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}

            logger.info("Try to register " + user.getEmail() + " to the system");
            response.setResponse(UserDTO.userToUserDTO(authService.addUser(user)));
            response.setMessage(registrationSuccessfulMessage);
            logger.info(registrationSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * checks if the email, password is valid, and send the user to the login method in AuthService
     *
     * @param user - the user's data
     * @return user with response body
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> login(@RequestBody User user) {
        Optional<CustomResponse<UserDTO>> isValid;
        CustomResponse<UserDTO> response = new CustomResponse<>(null, emptyString);
        try {
            isValid = checkValidEmail(user.getEmail(), response);
            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
            isValid = checkValidPassword(user.getPassword(), response);
            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}

            logger.info("Try to login : " + user.getEmail() + " to the system");
            response.setResponse(UserDTO.userToUserDTO(authService.login(user)));
            response.setHeaders(authService.getKeyEmailsValTokens().get(user.getEmail()));
            response.setMessage(loginSuccessfulMessage);
            logger.info(loginSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * checks if the name is valid, and send the user to the addGuest method in AuthService
     *
     * @param user - the user's data
     * @return user with response body
     */
    @RequestMapping(value = "login/guest", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> loginAsGuest(@RequestBody User user) {
        Optional<CustomResponse<UserDTO>> isValid;
        CustomResponse<UserDTO> response = new CustomResponse<>(null, emptyString);
        try {
            isValid = checkValidName(user.getName(), response);
            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}

            logger.info("Try to login as guest to the system");
            response.setResponse(UserDTO.userGuestToUserDTO(authService.addGuest(user)));
            response.setHeaders(authService.getKeyEmailsValTokens().get(user.getEmail()));
            response.setMessage(loginSuccessfulMessage);
            logger.info(loginSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * send the user to the verifyEmail method in AuthService
     *
     * @param user - the user's data
     * @return user with response body
     */
    @RequestMapping(value = "activate", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> verifyEmail(@RequestBody User user) {
        CustomResponse<UserDTO> response = new CustomResponse<>(null, emptyString);
        try {
            logger.info("try to activate email");
            response.setResponse(UserDTO.userToUserDTO(authService.verifyEmail(user)));
            response.setMessage(activationEmailSuccessfulMessage);
            logger.info(activationEmailSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
