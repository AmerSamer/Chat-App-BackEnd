package chatApp.service;

import static chatApp.Utilities.ExceptionHandler.*;
import static chatApp.Utilities.Utility.*;
import chatApp.entities.User;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private static JavaMailSender mailSender;

    @Autowired
    private SimpleMailMessage preConfiguredMessage;


    public UserService() {
    }

    public ResponseEntity<String> verifyEmail(User user) throws SQLDataException {
        User dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(user.getEmail()));
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
        userRepository.save(dbUser);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<String> updateUser(User user) throws SQLDataException {
        User dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser == null) {
            throw new SQLDataException(emailNotExistsMessage(user.getEmail()));
        }
//        dbUser.setEmail(user.getEmail()); //for now the user can not change his email until we provide his own token instead his email as primarykey
        if(user.getName() != null){
            dbUser.setName(user.getName());
        } else if (user.getPassword() != null) {
            dbUser.setPassword(encrypt(user.getPassword()));
        } else if (user.getDateOfBirth() != null) {
            dbUser.setDateOfBirth(user.getDateOfBirth());
            dbUser.setAge(calcAge(user.getDateOfBirth()));
        }else if(user.getPhoto() != null) {
            dbUser.setPhoto(user.getPhoto());
        }
        userRepository.save(dbUser);
        return ResponseEntity.ok().build();
    }
    private int calcAge (LocalDate dateOfBirth){
        return LocalDate.now().minusYears(dateOfBirth.getYear()).getYear();
    }

    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
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
