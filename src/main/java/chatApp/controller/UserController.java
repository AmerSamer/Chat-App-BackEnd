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

import static chatApp.Utilities.ExceptionHandler.*;
import static chatApp.Utilities.SuccessMessages.*;
import static chatApp.Utilities.Utility.*;

import java.sql.SQLDataException;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    public static Logger logger = LogManager.getLogger(UserController.class.getName());


    @Autowired
    private UserService userService;

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<CustomResponse<UserDTO>> updateUser(@RequestBody User user, @RequestParam String token) {
        try {
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
                if (user.getPassword() != null && !user.getPassword().equals("") && !isValidName(user.getName())) {
                    logger.error(invalidNameMessage);
                    CustomResponse<UserDTO> response = new CustomResponse<>(null, invalidNameMessage);
                    return ResponseEntity.badRequest().body(response);
                }
                logger.info("Try to update " + user.getEmail() + " in the system");
                User updateUser = userService.updateUser(user, token);
                UserDTO userDTO = userToUserDTO(updateUser);
                CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateUserSuccessfulMessage);
                logger.info("Update the client the update was successful");
                return ResponseEntity.ok().body(response);

        } catch (SQLDataException e) {
            logger.error(updateUserFailedMessage);
            CustomResponse<UserDTO> response = new CustomResponse<>(null, updateUserFailedMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }
        @RequestMapping(value = "logout", method = RequestMethod.POST)
        public ResponseEntity<CustomResponse<UserDTO>> logoutUser (@RequestParam String token){
            try {
                logger.info("User try to logout in the system");
                User logoutUser = userService.logoutUser(token);
                UserDTO userDTO = userToUserDTO(logoutUser);
                CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, logoutSuccessfulMessage);
                logger.info(logoutSuccessfulMessage);
                return ResponseEntity.ok().body(response);
            } catch (SQLDataException e) {
                logger.error(logoutUserFailedMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, logoutUserFailedMessage);
                return ResponseEntity.badRequest().body(response);
            }
        }

        @RequestMapping(value = "update/mute", method = RequestMethod.PATCH)
        public ResponseEntity<CustomResponse<UserDTO>> updateMuteUser (@RequestParam("token") String token,
                                                                       @RequestParam("id") Long id){
            try {
                logger.info("Try to mute / unmute user");
                User updateUser = userService.updateMuteUnmuteUser(id, token);
                UserDTO userDTO = userToUserDTO(updateUser);
                CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateMuteUnmuteUserSuccessfulMessage);
                logger.info(updateMuteUnmuteUserSuccessfulMessage);
                return ResponseEntity.ok().body(response);

            } catch (SQLDataException e) {
                CustomResponse<UserDTO> response = new CustomResponse<>(null, updateUserFailedMessage);
                return ResponseEntity.badRequest().body(response);
            }
        }

        @RequestMapping(value = "update/status", method = RequestMethod.PATCH)
        public ResponseEntity<CustomResponse<UserDTO>> updateStatusUser (@RequestParam("token") String token,
                                                                         @RequestParam("status") String status){
            try {
                User updateUser = userService.updateStatusUser(token, status);
                UserDTO userDTO = userToUserDTO(updateUser);
                CustomResponse<UserDTO> response = new CustomResponse<>(userDTO, updateStatusUserSuccessfulMessage);
                return ResponseEntity.ok().body(response);

            } catch (SQLDataException e) {
                logger.error(updateUserFailedMessage);
                CustomResponse<UserDTO> response = new CustomResponse<>(null, updateUserFailedMessage);
                return ResponseEntity.badRequest().body(response);
            }
        }
}
