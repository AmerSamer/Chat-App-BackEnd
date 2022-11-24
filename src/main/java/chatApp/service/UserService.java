package chatApp.service;

import static chatApp.Utilities.ExceptionHandler.*;
import static chatApp.Utilities.Utility.*;

import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;


    public UserService() {
    }

    public User updateUser(User user, String token) throws SQLDataException {
        String userEmail = authService.getKeyTokensValEmails().get(token);
        User dbUser = userRepository.findByEmail(userEmail);
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(user.getEmail()));
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
        return userRepository.save(dbUser);
    }

    public User logoutUser(String token) throws SQLDataException {
        String userEmail = authService.getKeyTokensValEmails().get(token);
        authService.getKeyTokensValEmails().remove(token);
        authService.getKeyEmailsValTokens().remove(userEmail);
        User dbUser = userRepository.findByEmail(userEmail);
        if (dbUser.getType().equals(UserType.GUEST)) {
            userRepository.delete(dbUser);
        } else {
            dbUser.setUserStatus(UserStatuses.OFFLINE);
        }
        return userRepository.save(dbUser);
    }

    private int calcAge(LocalDate dateOfBirth) {
        return LocalDate.now().minusYears(dateOfBirth.getYear()).getYear();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream().sorted(Comparator.comparing(User::getType)).collect(Collectors.toList());
    }

    public User updateMuteUnmuteUser(Long id, String token) throws SQLDataException {
        String userEmail = authService.getKeyTokensValEmails().get(token);
        if (userEmail == null) {
            throw new SQLDataException(tokenSessionExpired);
        }
        User dbUser = userRepository.getById(id);
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(dbUser.getEmail()));
        }
        if (!authService.getKeyEmailsValTokens().get(dbUser.getName()).equals(token)) {
            throw new SQLDataException(tokenSessionExpired);
        }
        if (dbUser.isMute()) {
            dbUser.setMute(false);
        } else {
            dbUser.setMute(true);
        }
        return userRepository.save(dbUser);
    }

    public User updateStatusUser(Long id, String token) throws SQLDataException {
        String userEmail = authService.getKeyTokensValEmails().get(token);
        if (userEmail == null) {
            throw new SQLDataException(tokenSessionExpired);
        }
        User dbUser = userRepository.getById(id);
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(dbUser.getEmail()));
        }
        if (!authService.getKeyEmailsValTokens().get(dbUser.getName()).equals(token)) {
            throw new SQLDataException(tokenSessionExpired);
        }
        if (dbUser.getUserStatus() == UserStatuses.ONLINE) {
            dbUser.setUserStatus(UserStatuses.AWAY);
        } else if (dbUser.getUserStatus() == UserStatuses.AWAY) {
            dbUser.setUserStatus(UserStatuses.ONLINE);
        }
        return userRepository.save(dbUser);
    }
}

