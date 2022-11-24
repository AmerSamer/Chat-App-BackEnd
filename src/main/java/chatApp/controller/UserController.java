package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static chatApp.Utilities.ExceptionHandler.*;
import static chatApp.Utilities.SuccessMessages.*;
import static chatApp.Utilities.Utility.*;

import java.sql.SQLDataException;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<CustomResponse<UserDTO>> updateUser(@RequestBody User user, @RequestParam String token) {
        try {
            if (user.getEmail() != null && !user.getEmail().equals("") && !isValidEmail(user.getEmail())) {
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidEmailMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (user.getPassword() != null && !user.getPassword().equals("") &&  !isValidPassword(user.getPassword())) {
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidPasswordMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (user.getPassword() != null && !user.getPassword().equals("") && !isValidName(user.getName())) {
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidNameMessage);
                return ResponseEntity.badRequest().body(response);
            }
            User updateUser = userService.updateUser(user,token);
            UserDTO userDTO = userToUserDTO(updateUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateUserSuccessfulMessage);
            return ResponseEntity.ok().body(response);

        } catch (SQLDataException e) {
            CustomResponse<UserDTO> response = new CustomResponse<>(null, updateUserFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> logoutUser(@RequestParam String token) {
        try {
            User logoutUser = userService.logoutUser(token);
            UserDTO userDTO = userToUserDTO(logoutUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, logoutSuccessfulMessage);
            return ResponseEntity.ok().body(response);

        } catch (SQLDataException e) {
            CustomResponse<UserDTO> response = new CustomResponse<>(null, logoutUserFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(value = "update/mute", params = {"id"}, method = RequestMethod.PATCH)
    public ResponseEntity<CustomResponse<UserDTO>> updateMuteUser(@RequestParam Long id, @RequestHeader String
            token) {
        try {
            User updateUser = userService.updateMuteUnmuteUser(id, token);
            UserDTO userDTO = userToUserDTO(updateUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateMuteUnmuteUserSuccessfulMessage);
            return ResponseEntity.ok().body(response);

        } catch (SQLDataException e) {
            CustomResponse<UserDTO> response = new CustomResponse<>(null, updateUserFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
