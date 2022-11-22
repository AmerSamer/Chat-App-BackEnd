package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static chatApp.Utilities.ExceptionHandler.*;
import static chatApp.Utilities.SuccessHandler.*;
import static chatApp.Utilities.Utility.*;

import java.sql.SQLDataException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "activate", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> verifyEmail(@RequestBody User user, @RequestHeader String token) {
        try {
            User userVerify = userService.verifyEmail(user,token);
            UserDTO userDTO = userToUserDTO(userVerify);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, activationEmailSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (SQLDataException e) {
            CustomResponse<UserDTO> response = new CustomResponse<>(null, activationEmailFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<UserDTO>>> getAllUsers() {
        List<User> userList = userService.getAllUsers();
        List<UserDTO> userListDTO = userListToUserListDTO(userList);
        CustomResponse<List<UserDTO>> response = new CustomResponse<>(userListDTO, listOfAllUsersSuccessfulMessage);
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<CustomResponse<UserDTO>> updateUser(@RequestBody User user) {
        try {
            User updateUser = userService.updateUser(user);
            UserDTO userDTO = userToUserDTO(updateUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateUserSuccessfulMessage);
            return ResponseEntity.ok().body(response);

        } catch (SQLDataException e) {
            CustomResponse<UserDTO> response = new CustomResponse<>(null, updateUserFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }


}
