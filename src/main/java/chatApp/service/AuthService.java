package chatApp.service;


import chatApp.utilities.Utility;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static chatApp.utilities.ExceptionMessages.*;
import static chatApp.utilities.Utility.*;

import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin
@Service
public class AuthService {

    private static Logger logger = LogManager.getLogger(AuthService.class.getName());
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SimpleMailMessage preConfiguredMessage;

    private Map<String, String> keyTokensValEmails;
    private Map<String, String> keyEmailsValTokens;

    /**
     * AuthService constructor
     * Initializes keyTokensValEmails new Map
     * Initializes keyEmailsValTokens new Map
     */
    AuthService() {
        this.keyTokensValEmails = getTokensInstance();
        this.keyEmailsValTokens = getEmailsInstance();
    }

    /**
     * Adds a user crypt password to the database if the user`s email exist in the db
     * @param user - the user's data
     * @return a saved user
     * @throws IllegalArgumentException when the provided email not exists in the database
     */
    public User login(User user) {
        try {
            logger.debug("Check if the user is exist in DB");
            if (userRepository.findByEmail(user.getEmail()) == null) {
                logger.error(emailNotExistsMessage(user.getEmail()));
                throw new IllegalArgumentException(emailNotExistsMessage(user.getEmail()));
            }
            User dbUser = User.dbUser(userRepository.findByEmail(user.getEmail()));

            logger.debug("Check if password of " + user.getEmail() + " are correct");
//        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
//        if (!bEncoder.matches(user.getPassword(), dbUser.getPassword())) {
//            logger.error(passwordDosentMatchMessage(user.getPassword()));
//            throw new SQLDataException(passwordDosentMatchMessage(user.getPassword()));
//        }
            logger.info("Create token for " + user.getEmail());
            String sessionToken = randomString();
            keyTokensValEmails.put(sessionToken, dbUser.getEmail());
            keyEmailsValTokens.put(dbUser.getEmail(), sessionToken);
            logger.info("User is logged into the system");
            dbUser.setUserStatus(UserStatuses.ONLINE);
            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Adds a user to the database if it has a unique name
     *
     * @param user - the user's data
     * @return a saved user
     * @throws IllegalArgumentException when the provided name exists in the database
     */
    public User addGuest(User user) {
        try {
            logger.debug("Check if the guest name is exist in DB");
            if (!userRepository.findByName(guestPrefix + user.getName()).isEmpty()) {
                logger.error(guestNameExistsMessage(user.getName()));
                throw new IllegalArgumentException(guestNameExistsMessage(user.getName()));
            }
            logger.info("The guest receives token,email,password");
            user.setEmail(user.getName() + "@chatappsystem.com");
            user.setName(guestPrefix + user.getName());
            user.setType(UserType.GUEST);
            user.setPassword(Utility.randomString());
            user.setUserStatus(UserStatuses.ONLINE);
            user.setNickname(user.getName());
            String sessionToken = randomString();
            keyTokensValEmails.put(sessionToken, user.getEmail());
            keyEmailsValTokens.put(user.getEmail(), sessionToken);
            logger.info("Save the guest in DB");
            return userRepository.save(user);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Adds a user to the database if it has a unique email
     *
     * @param user - the user's data
     * @return a saved user
     * @throws IllegalArgumentException when the provided email already exists
     */
    public User addUser(User user) {
        try {
            logger.debug("Check if the user is exist in DB");
            if (userRepository.findByEmail(user.getEmail()) != null) {
                logger.error(emailExistsInSystemMessage(user.getEmail()));
                throw new IllegalArgumentException(emailExistsInSystemMessage(user.getEmail()));
            }
            logger.info("Encrypts password user and sends him email to complete the registration");
            user.setPassword(encrypt((user.getPassword())));
            user.setType(UserType.GUEST);
            user.setNickname(user.getEmail());
            sendMessage(user);
            logger.info("User is Guest in the system, The system is waiting for activate email to complete the registration");
            return userRepository.save(user);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Adds a user to the database with userType field
     *
     * @param user - the user's data
     * @return a saved user
     * @throws IllegalArgumentException when the provided email not exists
     * @throws IllegalArgumentException when the provided user activation is true
     * @throws IllegalArgumentException when the provided token Expired
     */
    public User verifyEmail(User user) {
        try {
            logger.debug("Check if the user is exist in DB");
            if (userRepository.findByEmail(user.getEmail()) == null) {
                logger.error(emailNotExistsMessage(user.getEmail()));
                throw new IllegalArgumentException(emailNotExistsMessage(user.getEmail()));
            }

            User dbUser = User.dbUser(userRepository.findByEmail(user.getEmail()));

            logger.debug("Check if the user already activated");
            if (dbUser.isEnabled()) {
                logger.error(emailAlreadyActivatedMessage(user.getEmail()));
                throw new IllegalArgumentException(emailAlreadyActivatedMessage(user.getEmail()));
            } else if (LocalDate.now().isAfter(dbUser.getIssueDate().plusDays(1))) {
                logger.error(emailIssueTokenPassedMessage(user.getIssueDate().toString()));
                sendMessage(user);
                throw new IllegalArgumentException(emailIssueTokenPassedMessage(user.getIssueDate().toString()));
            } else if (!dbUser.getVerifyCode().equals(user.getVerifyCode())) {
                logger.error(verificationCodeNotMatch);
                throw new IllegalArgumentException(verificationCodeNotMatch);
            }

            dbUser.setEnabled(true);
            dbUser.setVerifyCode(null);
            dbUser.setType(UserType.REGISTERED);
            logger.info("Save the" + user.getEmail() + "in DB as registered user");
            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Chains a message and sends to an email with a token, uses the JAVAMAIL library
     *
     * @param user - the user's data
     */
    public void sendMessage(User user) {
        String verifyCode = randomString();
        user.setVerifyCode(verifyCode);
        user.setIssueDate(LocalDate.now());
        preConfiguredMessage.setFrom(systemEmail);
        preConfiguredMessage.setTo(user.getEmail());
        preConfiguredMessage.setSubject(emailContent);
        preConfiguredMessage.setText(verifyCode);
        mailSender.send(preConfiguredMessage);
    }

    /**
     * Initializes the keyTokensValEmails if the keyTokensValEmails is null
     */
    Map<String, String> getTokensInstance() {
        if (this.keyTokensValEmails == null)
            this.keyTokensValEmails = new HashMap<>();
        return this.keyTokensValEmails;
    }

    /**
     * Initializes the keyEmailsValTokens if the keyEmailsValTokens is null
     */
    Map<String, String> getEmailsInstance() {
        if (this.keyEmailsValTokens == null)
            this.keyEmailsValTokens = new HashMap<>();
        return this.keyEmailsValTokens;
    }

    /**
     * gets the KeyTokensValEmails Map
     */
    public Map<String, String> getKeyTokensValEmails() {
        return this.keyTokensValEmails;
    }

    /**
     * gets the KeyEmailsValTokens Map
     */
    public Map<String, String> getKeyEmailsValTokens() {
        return this.keyEmailsValTokens;
    }

}
