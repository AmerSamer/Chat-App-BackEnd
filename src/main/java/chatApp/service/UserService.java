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

    @Autowired
    private static JavaMailSender mailSender;

    @Autowired
    private SimpleMailMessage preConfiguredMessage;

    public UserService() {
    }

    public User verifyEmail(User user, String token) throws SQLDataException {
        User dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(user.getEmail()));
        }

        if (!authService.getKeyEmailsValTokens().get(user.getEmail()).equals(token)) {
            throw new SQLDataException(tokenSessionExpired);
        }

        if (dbUser.isEnabled()) {
            throw new SQLDataException(emailAlreadyActivatedMessage(user.getEmail()));
        } else if (LocalDate.now().isAfter(dbUser.getIssueDate().plusDays(1))) {
            sendMessage(user);
            throw new SQLDataException(emailIssueTokenPassedMessage(user.getIssueDate().toString()));
        } else if (!dbUser.getVerifyCode().equals(user.getVerifyCode())) {
            throw new SQLDataException(verificationCodeNotMatch);
        }

        dbUser.setEnabled(true);
        dbUser.setVerifyCode(null);
        dbUser.setType(UserType.REGISTERED);
        return userRepository.save(dbUser);
    }

    public User updateUser(User user, String token) throws SQLDataException {
        String userEmail = authService.getKeyTokensValEmails().get(token);
        User dbUser = userRepository.findByEmail(userEmail);
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(user.getEmail()));
        }

        if (!authService.getKeyEmailsValTokens().get(dbUser.getEmail()).equals(token)) {
            throw new SQLDataException(tokenSessionExpired);
        }

        if (user.getEmail() != null) {
            dbUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            dbUser.setName(user.getName());
        }
        if (user.getPassword() != null) {
            dbUser.setPassword(encrypt(user.getPassword()));
        }
        if (user.getDateOfBirth() != null) {
            dbUser.setDateOfBirth(user.getDateOfBirth());
            dbUser.setAge(calcAge(user.getDateOfBirth()));
        }
        if (user.getPhoto() != null) {
            dbUser.setPhoto(user.getPhoto());
        }
        return userRepository.save(dbUser);
    }

//    private int calcAge(LocalDate dateOfBirth) {

    public User logoutUser(User user) throws SQLDataException {
        System.out.println(user.getEmail());
        authService.getKeyEmailsValTokens().replace(user.getEmail(), null);
        System.out.println(authService.getKeyEmailsValTokens().get(user.getEmail()));
        User dbUser = userRepository.findByEmail(user.getEmail());
        dbUser.setUserStatus(UserStatuses.OFFLINE);
        return userRepository.save(dbUser);
    }


    private int calcAge(LocalDate dateOfBirth) {
        return LocalDate.now().minusYears(dateOfBirth.getYear()).getYear();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream().sorted(Comparator.comparing(User::getType)).collect(Collectors.toList());
    }

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
}

