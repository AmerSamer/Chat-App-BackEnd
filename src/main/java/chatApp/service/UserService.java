package chatApp.service;

import static chatApp.Utilities.ExceptionHandler.*;
import static chatApp.Utilities.Utility.*;

import chatApp.controller.AuthController;
import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;
import chatApp.repository.MessageRepository;
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

    public User updateUser(User user, String token) throws SQLDataException {
        logger.debug("Check if the user is exist in DB");
        String userEmail = authService.getKeyTokensValEmails().get(token);
        User dbUser = userRepository.findByEmail(userEmail);
        if (dbUser == null) {
            logger.error(emailNotExistsMessage(user.getEmail()));
            throw new SQLDataException(emailNotExistsMessage(user.getEmail()));
        }

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
            dbUser.setAge(calcAge(user.getDateOfBirth()));
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

    public User logoutUser(String token) throws SQLDataException {
        logger.info("Delete the user token");
        String userEmail = authService.getKeyTokensValEmails().get(token);
        authService.getKeyTokensValEmails().remove(token);
        authService.getKeyEmailsValTokens().remove(userEmail);
        User dbUser = userRepository.findByEmail(userEmail);
        logger.info("If the user guest delete him from DB else update his status to offline");
        if (dbUser.getType().equals(UserType.GUEST) && dbUser.getEmail().contains("chatappsystem")) {
            userRepository.delete(dbUser);
            return dbUser;
        }
        dbUser.setUserStatus(UserStatuses.OFFLINE);
        return userRepository.save(dbUser);
    }
    private int calcAge(LocalDate dateOfBirth) {
        return LocalDate.now().minusYears(dateOfBirth.getYear()).getYear();
    }

    public List<User> getAllUsers() {
        logger.info("Get all users in users table");
        return userRepository.findAll().stream().sorted(Comparator.comparing(User::getType)).collect(Collectors.toList());
    }

    public User updateMuteUnmuteUser(Long id, String token) throws SQLDataException {
        String userEmail = authService.getKeyTokensValEmails().get(token);
        if (userEmail == null) {
            logger.error(tokenSessionExpired);
            throw new SQLDataException(tokenSessionExpired);
        }
        User dbUser = userRepository.getById(id);
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(userEmail));
        }
        if (!authService.getKeyEmailsValTokens().get(userEmail).equals(token)) {
            throw new SQLDataException(tokenSessionExpired);
        }
        dbUser.setMute(!dbUser.isMute());
        return userRepository.save(dbUser);
    }
    public User updateStatusUser(String token, String status) throws SQLDataException {
        String userEmail = authService.getKeyTokensValEmails().get(token);
        if (userEmail == null) {
            throw new SQLDataException(tokenSessionExpired);
        }
        User dbUser = userRepository.findByEmail(userEmail);
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(userEmail));
        }
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
}

