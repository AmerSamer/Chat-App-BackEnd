package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;

import static chatApp.Utilities.ExceptionHandler.*;
import static chatApp.Utilities.SuccessHandler.*;
import static chatApp.Utilities.Utility.*;

@RestController
@CrossOrigin
@RequestMapping("/sign")
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> createUser(@RequestBody User user) {
        try {
            if (!isValidEmail(user.getEmail())) {
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidEmailMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (!isValidName(user.getName())) {
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidNameMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (!isValidPassword(user.getPassword())) {
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidPasswordMessage);
                return ResponseEntity.badRequest().body(response);
            }
            User createUser = authService.addUser(user);
            UserDTO userDTO = userToUserDTO(createUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, registrationSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (SQLDataException e) {
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
            User logUser = authService.login(user);
            UserDTO userDTO = userToUserDTO(logUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, loginSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (SQLDataException e) {
            CustomResponse<UserDTO> response = new CustomResponse<>(null, loginFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(value = "login/guest", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> loginAsGuest(@RequestBody User user) {
        try {
            if (!isValidName(user.getName())) {
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidNameMessage);
                return ResponseEntity.badRequest().body(response);
            }
            User userGuest = authService.addGuest(user);
            UserDTO userDTO = userGuestToUserDTO(userGuest);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, loginSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (SQLDataException e) {
            CustomResponse<UserDTO> response = new CustomResponse<>(null, loginAsGuestFailedMessage);
            return ResponseEntity.badRequest().body(response);
            //loginAsGuestFailedMessage;
            //maybe create out response entity with user and String message;
        }
    }
}
