package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            if (user.getEmail() != null && !user.getEmail().equals(emptyString) && !isValidEmail(user.getEmail())) {
                logger.error(invalidEmailMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidEmailMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (user.getPassword() != null && !user.getPassword().equals(emptyString) && !isValidPassword(user.getPassword())) {
                logger.error(invalidPasswordMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidPasswordMessage);
                return ResponseEntity.badRequest().body(response);
            }
            if (user.getName() != null && !user.getName().equals(emptyString) && !isValidName(user.getName())) {
                logger.error(invalidNameMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidNameMessage);
                return ResponseEntity.badRequest().body(response);
            }
            logger.info("Try to update " + user.getEmail() + " in the system");
            User updateUser = userService.updateUser(user, token);
            UserDTO userDTO = UserDTO.userToUserDTO(updateUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateUserSuccessfulMessage);
            logger.info("Update the client the update was successful");
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            CustomResponse<UserDTO> response = new CustomResponse<>(null, e.getMessage());
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
            User logoutUser = userService.logoutUser(token);
            UserDTO userDTO = UserDTO.userToUserDTO(logoutUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, logoutSuccessfulMessage);
            logger.info(logoutSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            CustomResponse<UserDTO> response = new CustomResponse<>(null, e.getMessage());
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
            User updateUser = userService.updateMuteUnmuteUser(id, token);
            UserDTO userDTO = UserDTO.userToUserDTO(updateUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateMuteUnmuteUserSuccessfulMessage);
            logger.info(updateMuteUnmuteUserSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            CustomResponse<UserDTO> response = new CustomResponse<>(null, e.getMessage());
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
            User updateUser = userService.updateStatusUser(token, status);
            UserDTO userDTO = UserDTO.userToUserDTO(updateUser);
            CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateStatusUserSuccessfulMessage);
            logger.info(updateStatusUserSuccessfulMessage);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            CustomResponse<UserDTO> response = new CustomResponse<>(null, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
