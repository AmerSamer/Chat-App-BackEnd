package chatApp.service;

import static chatApp.utilities.ExceptionMessages.*;
import static chatApp.utilities.Utility.*;

import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Service
public class UserService {
    private static Logger logger = LogManager.getLogger(UserService.class.getName());

    @Autowired
    private AuthService authService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    public UserService() {
    }

    /**
     * Update user : check if data is valid syntax & the user exist in DB, update user data in DB
     *
     * @param user  - the user's data
     * @param token - the token of the user
     * @return user with updated data
     * @throws IllegalArgumentException when the Update user failed
     */
    public User updateUser(User user, String token) {
        try {
            logger.debug("Check if the user is exist in DB");
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userRepository.findByEmail(userEmail) == null) {
                logger.error(emailNotExistsMessage(user.getEmail()));
                throw new IllegalArgumentException(emailNotExistsMessage(user.getEmail()));
            }
            User dbUser = User.dbUser(userRepository.findByEmail(userEmail));

            if (!user.getEmail().equals(emptyString)) {
                messageService.updateUserEmailMessages(dbUser.getEmail(), user.getEmail());
                if (dbUser.getNickname().equals(dbUser.getEmail())) {
                    dbUser.setNickname(user.getEmail());
                }
                dbUser.setEmail(user.getEmail());
            }
            if (user.getNickname() != null && !user.getNickname().equals(emptyString)) {
                messageService.updateUserNicknameMessages(dbUser.getNickname(), user.getNickname());
                dbUser.setNickname(user.getNickname());
            }
            if (user.getName() != null && !user.getName().equals(emptyString)) {
                if(!isValidName(user.getName())){
                    throw new IllegalArgumentException(updateUserFailedMessage + invalidNameMessage);
                }
                dbUser.setName(user.getName());
            }
            if (user.getPassword() != null && !user.getPassword().equals(emptyString)) {
                dbUser.setPassword(encrypt(user.getPassword()));
            }
            if (user.getDateOfBirth() != null) {
                if(user.getDateOfBirth().isAfter(LocalDate.now())){
                    throw new IllegalArgumentException(updateUserFailedMessage + invalidDateMessage);
                }
                dbUser.setDateOfBirth(user.getDateOfBirth());
                dbUser.setAge(calcAge(user.getDateOfBirth()));
            }
            if (user.getPhoto() != null && !user.getPhoto().equals(emptyString)) {
                dbUser.setPhoto(user.getPhoto());
            }
            if (user.getDescription() != null && !user.getDescription().equals(emptyString)) {
                dbUser.setDescription(user.getDescription());
            }
            logger.info("Update the user, and save updating in DB");

            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            logger.error("Update the user failed");
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Logout user : delete token & change status to offline, if the user is guest delete him from the DB
     *
     * @param token - the token of the user
     * @return user with offline status
     * @throws IllegalArgumentException when the logout user failed
     */
    public User logoutUser(String token) {
        try {
            logger.info("Delete the user token");
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userRepository.findByEmail(userEmail) == null) {
                logger.error(emailNotExistsMessage);
                throw new IllegalArgumentException(emailNotExistsMessage);
            }
            authService.getKeyTokensValEmails().remove(token);
            authService.getKeyEmailsValTokens().remove(userEmail);
            User dbUser = User.dbUser(userRepository.findByEmail(userEmail));
            logger.info("If the user is a guest delete him from DB else update his status to offline");
            if (dbUser.getType().equals(UserType.GUEST) && dbUser.getEmail().contains(systemEmail)) {
                userRepository.delete(dbUser);
                return dbUser;
            }
            dbUser.setUserStatus(UserStatuses.OFFLINE);
            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            logger.error("logout the user failed");
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Update Mute/unmute Users : check token session not expired & the user exist in DB, update user mute/unmute status in DB
     *
     * @param token - the token of the user
     * @param id    - the id of the user
     * @return user with mute/unmute status
     * @throws IllegalArgumentException when the update mute/unmute user failed
     */
    public User updateMuteUnmuteUser(Long id, String token) {
        try {
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userEmail == null) {
                logger.error(tokenSessionExpired);
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            if (userRepository.findByEmail(userEmail).getType() != UserType.ADMIN) {
                logger.error(notAdminUser);
                throw new IllegalArgumentException(notAdminUser);
            }
            if (userRepository.getById(id) == null) {
                throw new IllegalArgumentException(emailNotExistsMessage(userEmail));
            }

            User dbUser = User.dbUser(userRepository.getById(id));

            if (!authService.getKeyEmailsValTokens().get(userEmail).equals(token)) {
                throw new IllegalArgumentException(tokenSessionExpired);
            }

            dbUser.setMute(!dbUser.isMute());
            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            logger.error("mute/unmute the user failed");
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Update away/online Users : check token session not expired & the user exist in DB, update user away/online status in DB
     *
     * @param token  - the token of the user
     * @param status - the away/online status of the user
     * @return user with away/online status
     * @throws IllegalArgumentException when the update away/online status user failed
     */
    public User updateStatusUser(String token, String status) {
        try {
            String userEmail = authService.getKeyTokensValEmails().get(token);
            if (userEmail == null) {
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            if (userRepository.findByEmail(userEmail) == null) {
                throw new IllegalArgumentException(emailNotExistsMessage(userEmail));
            }

            User dbUser = User.dbUser(userRepository.findByEmail(userEmail));

            if (!authService.getKeyEmailsValTokens().get(dbUser.getEmail()).equals(token)) {
                throw new IllegalArgumentException(tokenSessionExpired);
            }
            if (status.equals(UserStatuses.AWAY.name().toLowerCase())) {
                dbUser.setUserStatus(UserStatuses.AWAY);
            } else if (status.equals(UserStatuses.ONLINE.name().toLowerCase())) {
                dbUser.setUserStatus(UserStatuses.ONLINE);
            }
            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            logger.error("Update status for user failed");
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get all users: get all users from DB
     *
     * @return all the users sorted by theirs types [ADMIN(0), REGISTERED(1), GUEST(2)] from DB
     */
    public List<User> getAllUsers() {
        logger.info("Get all users in users table sorted by admin,registered,guest and filtered the offline users");
        return userRepository.findAll().stream().filter(currUser -> currUser.getUserStatus() != UserStatuses.OFFLINE).sorted(Comparator.comparing(User::getType)).collect(Collectors.toList());
    }
}

