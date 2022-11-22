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
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        if(!authService.getKeyEmailsValTokens().get(user.getEmail()).equals(token)){
            throw new SQLDataException(tokenSessionExpired);
        }

        if(dbUser.isEnabled()){
            throw new SQLDataException(emailAlreadyActivatedMessage(user.getEmail()));
        }
        else if(LocalDate.now().isAfter(dbUser.getIssueDate().plusDays(1))){
            sendMessage(user);
            throw new SQLDataException(emailIssueTokenPassedMessage(user.getIssueDate().toString()));
        }
        else if(!dbUser.getVerifyCode().equals(user.getVerifyCode())){
            throw new SQLDataException(verificationCodeNotMatch);
        }

        dbUser.setEnabled(true);
        dbUser.setVerifyCode(null);
        dbUser.setType(UserType.REGISTERED);
        return userRepository.save(dbUser);
    }

    public User updateUser(User user) throws SQLDataException {
        User dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(user.getEmail()));
        }
//        dbUser.setEmail(user.getEmail()); //for now the user can not change his email until we provide his own token instead his email as primarykey
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

    public List<User> getAllUsers() {
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
