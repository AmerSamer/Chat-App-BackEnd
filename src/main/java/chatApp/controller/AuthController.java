package chatApp.controller;

import chatApp.entities.User;
import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;

import static chatApp.Utilities.Utility.*;

@RestController
@CrossOrigin
@RequestMapping("/sign")
public class AuthController {

    @Autowired
    private AuthService authService;

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
            return ResponseEntity.ok(authService.addUser(user).toString());
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email already exists", e);
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            return authService.login(user);
        } catch (SQLDataException e) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Email or Password are wrong", e);
        }
    }

    @RequestMapping(value = "login/guest", method = RequestMethod.POST)
    public ResponseEntity<User> loginAsGuest(@RequestBody User user) {
        try {
            if (!isValidName(user.getName())) {
                return ResponseEntity.badRequest().body(user);
            }
            return ResponseEntity.ok(authService.addGuest(user));
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Name already exists!", e);
        }
    }

}
