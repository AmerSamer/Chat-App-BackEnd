package chatApp.service;

import static chatApp.Utilities.ExceptionHandler.*;
import static chatApp.Utilities.Utility.*;
import chatApp.entities.User;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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


    public User updateUser(User user, String token) throws SQLDataException {
        String userEmail = authService.getKeyTokensValEmails().get(token);
        User dbUser = userRepository.findByEmail(userEmail);
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(user.getEmail()));
        }
        if(user.getEmail() != null){
            dbUser.setEmail(user.getEmail());
        }
        if(user.getName() != null){
            dbUser.setName(user.getName());
        }
        if (user.getPassword() != null) {
            dbUser.setPassword(encrypt(user.getPassword()));
        }
        if (user.getDateOfBirth() != null) {
            dbUser.setDateOfBirth(user.getDateOfBirth());
            dbUser.setAge(calcAge(user.getDateOfBirth()));
        }
        if(user.getPhoto() != null) {
            dbUser.setPhoto(user.getPhoto());
        }
        return userRepository.save(dbUser);
    }
    private int calcAge (LocalDate dateOfBirth){
        return LocalDate.now().minusYears(dateOfBirth.getYear()).getYear();
    }

    public List<User> getAllUsers(){
        return userRepository.findAll().stream().sorted(Comparator.comparing(User::getType)).collect(Collectors.toList());
    }

    public void sendMessage(User user){
        String verifyCode = randomString();
        user.setVerifyCode(verifyCode);
        user.setIssueDate(LocalDate.now());
        preConfiguredMessage.setFrom(systemEmail);
        preConfiguredMessage.setTo(user.getEmail());
        preConfiguredMessage.setSubject(emailContent);
        preConfiguredMessage.setText(verifyCode);
        mailSender.send(preConfiguredMessage);
    }
}
