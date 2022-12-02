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

import java.time.LocalDate;
import java.util.List;

import static chatApp.utilities.ExceptionMessages.*;
import static chatApp.utilities.SuccessMessages.*;
import static chatApp.utilities.Utility.*;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    private static Logger logger = LogManager.getLogger(UserController.class.getName());
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
        try {
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userEmail == null) {
                logger.error(tokenSessionExpired);
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            if (user.getEmail() != null && !user.getEmail().equals("") && !isValidEmail(user.getEmail())) {
                logger.error(invalidEmailMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidEmailMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (user.getPassword() != null && !user.getPassword().equals("") && !isValidPassword(user.getPassword())) {
                logger.error(invalidPasswordMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidPasswordMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (user.getName() != null && !user.getName().equals("") && !isValidName(user.getName())) {
                logger.error(invalidNameMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidNameMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (user.getDateOfBirth() != null) {
                if (user.getDateOfBirth().isAfter(LocalDate.now())) {
                    logger.error(updateUserFailedMessage + ", invalid date");
                    CustomResponse<UserDTO> response = new CustomResponse<>(null, updateUserFailedMessage + ", invalid date");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            logger.info("Try to update " + user.getEmail() + " in the system");
            User updateUser = userService.updateUser(user, userEmail);
            UserDTO userDTO = UserDTO.userToUserDTO(updateUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateUserSuccessfulMessage);
            logger.info("Update the client the update was successful");
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(updateUserFailedMessage);
            CustomResponse<UserDTO> response = new CustomResponse<>(null, updateUserFailedMessage);
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
        try {
            logger.info("User try to logout in the system");
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
            UserDTO userDTO = UserDTO.userToUserDTO(logoutUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, logoutSuccessfulMessage);
            logger.info(logoutSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(logoutUserFailedMessage);
            CustomResponse<UserDTO> response = new CustomResponse<>(null, logoutUserFailedMessage);
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
        try {
            logger.info("Try to mute / unmute user");
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userEmail == null) {
                logger.error(tokenSessionExpired);
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            if (!authService.getKeyEmailsValTokens().get(userEmail).equals(token)) {
                throw new IllegalArgumentException(tokenSessionExpired);
            }

            User updateUser = userService.updateMuteUnmuteUser(id, userEmail);
            UserDTO userDTO = UserDTO.userToUserDTO(updateUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateMuteUnmuteUserSuccessfulMessage);
            logger.info(updateMuteUnmuteUserSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(muteUserFailedMessage);
            CustomResponse<UserDTO> response = new CustomResponse<>(null, muteUserFailedMessage);
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
        try {
            logger.info("Try to changed the status of the user to ONLINE/AWAY");
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userEmail == null) {
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            if (!authService.getKeyEmailsValTokens().get(userEmail).equals(token)) {
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            User updateUser = userService.updateStatusUser(userEmail, status);
            UserDTO userDTO = UserDTO.userToUserDTO(updateUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateStatusUserSuccessfulMessage);
            logger.info(updateStatusUserSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(updateStatusUserFailedMessage);
            CustomResponse<UserDTO> response = new CustomResponse<>(null, updateStatusUserFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
