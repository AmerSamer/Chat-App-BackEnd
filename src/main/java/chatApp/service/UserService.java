package chatApp.service;

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
            throw new SQLDataException(String.format("Email %s doesn't exists in users table", user.getEmail()));
        }

        if(dbUser.isEnabled()){
            throw new SQLDataException("User already activate");
        }
        else if(LocalDate.now().isAfter(dbUser.getIssueDate().plusDays(1))){
            sendMessage(user);
            throw new SQLDataException("More than 24 hours passed from last verification code, new verification code has been sent");
        }
        else if(!dbUser.getVerifyCode().equals(user.getVerifyCode())){
            throw new SQLDataException("Verification code doesn't match");

        }

        dbUser.setEnabled(true);
        dbUser.setVerifyCode(null);
        dbUser.setType(UserType.REGISTERED);
        userRepository.save(dbUser);
        return ResponseEntity.ok("success, email activate");
    }

    public ResponseEntity<String> updateUser(User user) throws SQLDataException {
        User dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser == null) {
            throw new SQLDataException(String.format("Email %s doesn't exists in users table", user.getEmail()));
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
        return ResponseEntity.ok("user has updated successfully");
    }
    private int calcAge (LocalDate dateOfBirth){
        return LocalDate.now().minusYears(dateOfBirth.getYear()).getYear();
    }

    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    public void sendMessage(User user){
        String verifyCode = randomString();
        user.setVerifyCode(encrypt(verifyCode));
        user.setIssueDate(LocalDate.now());
        String from = "seselevtion@gmail.com";
        String to = user.getEmail();
        preConfiguredMessage.setFrom(from);
        preConfiguredMessage.setTo(to);
        preConfiguredMessage.setSubject("Chat App Verification Code");
        preConfiguredMessage.setText(verifyCode);
        mailSender.send(preConfiguredMessage);
    }
}
