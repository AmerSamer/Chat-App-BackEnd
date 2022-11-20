package chatApp.controller;

import chatApp.entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public String createUser(@RequestBody User user) {
        try {
            return userService.addUser(user).toString();
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email already exists", e);
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            return userService.login(user);
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email or Password are wrong", e);
        }
    }
}
