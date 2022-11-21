package chatApp.controller;

import chatApp.entities.User;
import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import static chatApp.Utilities.ExceptionHandler.*;
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
                return ResponseEntity.badRequest().body(invalidEmailMessage);
            }
            if (!isValidName(user.getName())) {
                return ResponseEntity.badRequest().body(invalidNameMessage);
            }
            if (!isValidPassword(user.getPassword())) {
                return ResponseEntity.badRequest().body(invalidPasswordMessage);
            }
            return ResponseEntity.ok(authService.addUser(user).toString());
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(emailExistsInSystemMessage(user.getEmail()));
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            return authService.login(user);
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body(loginFailedMessage);
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
            return ResponseEntity.badRequest().body(user);
            //loginAsGuestFailedMessage;
            //maybe create out response entity with user and String message;
        }
    }

}
