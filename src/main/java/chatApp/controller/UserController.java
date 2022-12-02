package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.service.AuthService;
import chatApp.service.MessageService;
import chatApp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static chatApp.utilities.LoggerMessages.*;
import java.time.LocalDate;
import java.util.List;

import static chatApp.utilities.ExceptionMessages.*;
import static chatApp.utilities.SuccessMessages.*;
import static chatApp.utilities.Utility.*;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class.getName());
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @Autowired
    private MessageService messageService;

    /**
     * Update user : check if data is valid syntax & the user exist in DB, update user data in DB
     *
     * @param user  - the user's data
     * @param token - the token of the user
     * @return user with updated data
     */
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<CustomResponse<UserDTO>> updateUser(@RequestBody User user, @RequestParam String token) {
        Optional<CustomResponse<UserDTO>> isValid;
        CustomResponse<UserDTO> response = new CustomResponse<>(null, emptyString);
        try {
            logger.info(beforeAnAction(user.getEmail(), "update"));
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userEmail == null) {
                logger.error(tokenSessionExpired);
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            isValid = checkValidEmail(user.getEmail(), response);
            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
            isValid = checkValidPassword(user.getPassword(), response);
            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}
            isValid = checkValidName(user.getName(), response);
            if(isValid.isPresent()){ return ResponseEntity.badRequest().body(isValid.get());}

            response.setResponse(UserDTO.userToUserDTO(userService.updateUser(user, token)));
            response.setMessage(updateUserSuccessfulMessage);
            logger.info(updateUserSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Logout user : delete token & change status to offline, if the user is guest delete him from the DB
     *
     * @param token - the token of the user
     * @return user with offline status
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ResponseEntity<CustomResponse<UserDTO>> logoutUser(@RequestParam String token) {
        CustomResponse<UserDTO> response = new CustomResponse<>(null, emptyString);
        try {
            logger.info(beforeLogout);
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userEmail == null) {
                logger.error(tokenSessionExpired);
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            User logoutUser = userService.logoutUser(userEmail);
            if (logoutUser != null) {
                authService.getKeyTokensValEmails().remove(token);
                authService.getKeyEmailsValTokens().remove(userEmail);
            }
            response.setResponse(UserDTO.userToUserDTO(userService.logoutUser(token)));
            response.setMessage(logoutSuccessfulMessage);
            logger.info(logoutSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Update Mute/unmute Users : check token session not expired & the user exist in DB, update user mute/unmute status in DB
     *
     * @param token - the token of the user
     * @param id    - the id of the user
     * @return user with mute/unmute status
     */
    @RequestMapping(value = "update/mute", method = RequestMethod.PATCH)
    public ResponseEntity<CustomResponse<UserDTO>> updateMuteUser(@RequestParam("token") String token, @RequestParam("id") Long id) {
        CustomResponse<UserDTO> response = new CustomResponse<>(null, emptyString);
        try {
            logger.info(beforeMuteUnmute);
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userEmail == null) {
                logger.error(tokenSessionExpired);
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            if (!authService.getKeyEmailsValTokens().get(userEmail).equals(token)) {
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            response.setResponse(UserDTO.userToUserDTO(userService.updateMuteUnmuteUser(id, token)));
            response.setMessage(updateMuteUnmuteUserSuccessfulMessage);
            logger.info(updateMuteUnmuteUserSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Update away/online Users : check token session not expired & the user exist in DB, update user away/online status in DB
     *
     * @param token  - the token of the user
     * @param status - the away/online status of the user
     * @return user with away/online status
     * ]
     */
    @RequestMapping(value = "update/status", method = RequestMethod.PATCH)
    public ResponseEntity<CustomResponse<UserDTO>> updateStatusUser(@RequestParam("token") String token, @RequestParam("status") String status) {
        CustomResponse<UserDTO> response = new CustomResponse<>(null, emptyString);
        try {
            logger.info(beforeUpdateStatus);
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userEmail == null) {
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            if (!authService.getKeyEmailsValTokens().get(userEmail).equals(token)) {
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            response.setResponse(UserDTO.userToUserDTO(userService.updateStatusUser(token, status)));
            response.setMessage(updateStatusUserSuccessfulMessage);
            logger.info(updateStatusUserSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
