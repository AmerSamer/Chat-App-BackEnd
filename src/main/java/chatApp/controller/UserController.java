package chatApp.controller;

import chatApp.entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static chatApp.Utilities.ExceptionHandler.*;
import java.sql.SQLDataException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "activate", method = RequestMethod.POST)
    public ResponseEntity<String> verifyEmail(@RequestBody User user){
        try {
            return userService.verifyEmail(user);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(activationEmailFailedMessage);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }


    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<String> updateUser(@RequestBody User user){
        try {
            return userService.updateUser(user);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(updateUserFailedMessage);
        }
    }


}
