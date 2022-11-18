package chatApp.controller;

import chatApp.entities.User;
import chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static chatApp.Utilities.Utility.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @RequestMapping(value = "register", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> register(@RequestBody User user) {

        if (!isValidEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid Email!");
        }
        if (!isValidName(user.getName())) {
            return ResponseEntity.badRequest().body("Invalid Name!");
        }
        if (!isValidPassword(user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid Password!");
        }
        boolean success = authService.register(user);

        if (success) {
            return ResponseEntity.ok("User name: " + user.getName() + " was registered");
        } else {
            return ResponseEntity.badRequest().body("User name: " + user.getName() + " was not registered");
        }
    }
    @RequestMapping(value = "login", method = RequestMethod.POST, consumes = "application/json")
    public String login(@RequestBody String email, @RequestBody String password) {

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Your email address is invalid!");
        }
        return authService.login(email, password);
    }

}
