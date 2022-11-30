package chatApp.service;

import static chatApp.Utilities.ExceptionMessages.*;
import static chatApp.Utilities.Utility.*;

import chatApp.Utilities.Utility;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.sql.SQLDataException;
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
    private UserRepository userRepository;



    public UserService() {
    }

    /**
     *Update user : check if data is valid syntax & the user exist in DB, update user data in DB
     * @param user - the user's data
     * @param token - the token of the user
     * @return user with updated data
     * @throws SQLDataException when the Update user failed
     */
    public User updateUser(User user, String token) throws SQLDataException {
        logger.debug("Check if the user is exist in DB");
        String userEmail = authService.getKeyTokensValEmails().get(token);
        if (userRepository.findByEmail(userEmail) == null) {
            logger.error(emailNotExistsMessage(user.getEmail()));
            throw new SQLDataException(emailNotExistsMessage(user.getEmail()));
        }
        User dbUser = User.dbUser(userRepository.findByEmail(userEmail));

        if (!user.getNickname().equals("")) {
            dbUser.setNickname(user.getNickname());
        }
        if (!user.getEmail().equals("")) {
            dbUser.setEmail(user.getEmail());
        }
        if (!user.getName().equals("")) {
            dbUser.setName(user.getName());
        }
        if (!user.getPassword().equals("")) {
            dbUser.setPassword(encrypt(user.getPassword()));
        }
        if (user.getDateOfBirth() != null) {
            dbUser.setDateOfBirth(user.getDateOfBirth());
            dbUser.setAge(Utility.calcAge(user.getDateOfBirth()));
        }
        if (!user.getPhoto().equals("")) {
            dbUser.setPhoto(user.getPhoto());
        }
        if (!user.getDescription().equals("")) {
            dbUser.setDescription(user.getDescription());
        }
        logger.info("Update the user, and save updating in DB");
        return userRepository.save(dbUser);
    }

    /**
     *Logout user : delete token & change status to offline, if the user is guest delete him from the DB
     * @param token - the token of the user
     * @return user with offline status
     * @throws SQLDataException when the logout user failed
     */
    public User logoutUser(String token) throws SQLDataException {
        logger.info("Delete the user token");
        String userEmail = authService.getKeyTokensValEmails().get(token);
        authService.getKeyTokensValEmails().remove(token);
        authService.getKeyEmailsValTokens().remove(userEmail);
        User dbUser = User.dbUser(userRepository.findByEmail(userEmail));
        logger.info("If the user guest delete him from DB else update his status to offline");
        if (dbUser.getType().equals(UserType.GUEST) && dbUser.getEmail().contains("chatappsystem")) {
            userRepository.delete(dbUser);
            return dbUser;
        }
        dbUser.setUserStatus(UserStatuses.OFFLINE);
        return userRepository.save(dbUser);
    }
    /**
     *Update Mute/unmute Users : check token session not expired & the user exist in DB, update user mute/unmute status in DB
     * @param token - the token of the user
     * @param id - the id of the user
     * @return user with mute/unmute status
     * @throws SQLDataException when the update mute/unmute user failed
     */
    public User updateMuteUnmuteUser(Long id, String token) throws SQLDataException {
        String userEmail = authService.getKeyTokensValEmails().get(token);
        if (userEmail == null) {
            logger.error(tokenSessionExpired);
            throw new SQLDataException(tokenSessionExpired);
        }
        if (userRepository.getById(id) == null) {
            throw new SQLDataException(emailNotExistsMessage(userEmail));
        }

        User dbUser = User.dbUser(userRepository.getById(id));

        if (!authService.getKeyEmailsValTokens().get(userEmail).equals(token)) {
            throw new SQLDataException(tokenSessionExpired);
        }

        dbUser.setMute(!dbUser.isMute());
        return userRepository.save(dbUser);
    }
    /**
     *Update away/online Users : check token session not expired & the user exist in DB, update user away/online status in DB
     * @param token - the token of the user
     * @param status - the away/online status of the user
     * @return user with away/online status
     * @throws SQLDataException when the update away/online status user failed
     */
    public User updateStatusUser(String token, String status) throws SQLDataException {
        String userEmail = authService.getKeyTokensValEmails().get(token);
        if (userEmail == null) {
            throw new SQLDataException(tokenSessionExpired);
        }
        if (userRepository.findByEmail(userEmail) == null) {
            throw new SQLDataException(emailNotExistsMessage(userEmail));
        }

        User dbUser = User.dbUser(userRepository.findByEmail(userEmail));

        if (!authService.getKeyEmailsValTokens().get(dbUser.getEmail()).equals(token)) {
            throw new SQLDataException(tokenSessionExpired);
        }
        if (status.equals("away")) {
            dbUser.setUserStatus(UserStatuses.AWAY);
        } else if (status.equals("online")) {
            dbUser.setUserStatus(UserStatuses.ONLINE);
        }
        return userRepository.save(dbUser);
    }

    /**
     *Get all users: get all users from DB
     * @return all the users sorted by theirs types [ADMIN(0), REGISTERED(1), GUEST(2)] from DB
     */
    public List<User> getAllUsers() {
        logger.info("Get all users in users table");
        return userRepository.findAll().stream().filter(currUser -> currUser.getUserStatus() != UserStatuses.OFFLINE).sorted(Comparator.comparing(User::getType)).collect(Collectors.toList());
    }


}

