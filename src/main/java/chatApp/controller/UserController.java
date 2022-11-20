package chatApp.controller;

import chatApp.entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;

import static chatApp.Utilities.Utility.*;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody User user){
        try {
            if (!isValidEmail(user.getEmail())) {
                return ResponseEntity.badRequest().body("Invalid Email!");
        }
        if (!isValidName(user.getName())) {
            return ResponseEntity.badRequest().body("Invalid Name!");
        }
        if (!isValidPassword(user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid Password!");
        }
            return ResponseEntity.ok(userService.addUser(user).toString());
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email already exists", e);
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            return userService.login(user);
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email or Password are wrong", e);
        }
    }

    @RequestMapping(value = "activate", method = RequestMethod.POST)
    public ResponseEntity<String> verifyEmail(@RequestBody User user, @RequestBody String verifyCode){
        return userService.verifyEmail(user, verifyCode);
    }
}
