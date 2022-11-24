package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.web.server.ServerSecurityMarker;
import org.springframework.web.bind.annotation.*;

import static chatApp.Utilities.ExceptionHandler.*;
import static chatApp.Utilities.SuccessMessages.*;
import static chatApp.Utilities.Utility.*;

import java.sql.SQLDataException;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<CustomResponse<UserDTO>> updateUser(@RequestBody User user, @RequestHeader String token) {
        try {
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
    public ResponseEntity<CustomResponse<UserDTO>> logoutUser(@RequestBody User user) {
        try {
            User logoutUser = userService.logoutUser(user);
            UserDTO userDTO = userToUserDTO(logoutUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, logoutSuccessfulMessage);
            return ResponseEntity.ok().body(response);

        } catch (SQLDataException e) {
            CustomResponse<UserDTO> response = new CustomResponse<>(null, logoutUserFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }


    }
}
