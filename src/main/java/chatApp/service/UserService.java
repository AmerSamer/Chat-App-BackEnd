package chatApp.service;

import static chatApp.utilities.ExceptionMessages.*;
import static chatApp.utilities.Utility.*;

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

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Service
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class.getName());

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;


    /**
     * Update user : check if data is valid syntax & the user exist in DB, update user data in DB
     *
     * @param user  - the user's data
     * @return user with updated data
     * @throws IllegalArgumentException when the Update user failed
     */
    public User updateUser(User user, String userEmail) {
        try {
            logger.debug("Check if the user is exist in DB");
            User dbUser = User.dbUser(userRepository.findByEmail(userEmail));
            if (!user.getEmail().equals(emptyString)) {
                updateUserMessages(dbUser.getEmail(), user.getEmail());
                if (dbUser.getNickname().equals(dbUser.getEmail())) {
                    dbUser.setNickname(user.getEmail());
                }
                dbUser.setEmail(user.getEmail());
            }
            if (user.getNickname() != null && !user.getNickname().equals(emptyString)) {
                updateUserMessages(dbUser.getNickname(), user.getNickname());
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
     * @param userEmail - the userEmail gets by the token
     * @return user with offline status
     * @throws IllegalArgumentException when the logout user failed
     */
    public User logoutUser(String userEmail) {
        try {
//            logger.info("Delete the user token");
            User user = userRepository.findByEmail(userEmail);
            if (user == null) {
                logger.error(emailNotExistsMessage);
                throw new IllegalArgumentException(emailNotExistsMessage);
            }
            User dbUser = User.dbUser(user);
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
     * @param userEmail - the userEmail from the token hash
     * @param id    - the id of the user
     * @return user with mute/unmute status
     * @throws IllegalArgumentException when the update mute/unmute user failed
     */
    public User updateMuteUnmuteUser(Long id, String userEmail) {
        try {
            if (userRepository.findByEmail(userEmail).getType() != UserType.ADMIN) {
                logger.error(notAdminUser);
                throw new IllegalArgumentException(notAdminUser);
            }
            User user = userRepository.getById(id);
            if (user == null) {
                throw new IllegalArgumentException(emailNotExistsMessage(userEmail));
            }
            User dbUser = User.dbUser(user);
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
     * @param userEmail  - the userEmail from the token hash
     * @param status - the away/online status of the user
     * @return user with away/online status
     * @throws IllegalArgumentException when the update away/online status user failed
     */
    public User updateStatusUser(String userEmail, String status) {
        try {
            User user = userRepository.findByEmail(userEmail);
            if (user == null) {
                throw new IllegalArgumentException(emailNotExistsMessage(userEmail));
            }
            User dbUser = User.dbUser(user);
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


    /**
     * Update user nickname messages by sender and receiver
     * @param oldNickname - previous user email
     * @param newNickname - new user email
     */
    public void updateUserMessages(String oldNickname, String newNickname) {
        List<Message> senderMessages = messageRepository.findBySender(oldNickname);
        List<Message> newSenderMessages = senderMessages.stream().filter(message -> message.getSender().equals(oldNickname)).collect(Collectors.toList());
        newSenderMessages.forEach(message -> message.setSender(newNickname));
        newSenderMessages.forEach(message -> messageRepository.save(message));

        List<Message> receiverMessages = messageRepository.findByReceiver(oldNickname);
        List<Message> newReceiverMessages = receiverMessages.stream().filter(message -> message.getReceiver().equals(oldNickname)).collect(Collectors.toList());
        newReceiverMessages.forEach(message -> message.setReceiver(newNickname));
        newReceiverMessages.forEach(message -> messageRepository.save(message));
    }
}

