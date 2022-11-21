package chatApp.service;


import chatApp.Utilities.Utility;
import chatApp.entities.User;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.time.LocalDate;

import static chatApp.Utilities.Utility.encrypt;
import static chatApp.Utilities.Utility.randomString;


//import static chatApp.Utilities.Utility.*;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SimpleMailMessage preConfiguredMessage;


    public ResponseEntity<String> login(User user) throws SQLDataException {
        User dbUser = userRepository.findByEmail(user.getEmail());
        if (dbUser == null) {
            throw new SQLDataException(String.format("Email %s doesn't exists in users table", user.getEmail()));
        }
        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
        if (!bEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new SQLDataException(String.format("Password %s doesn't match to email", user.getEmail()));
        }
        return ResponseEntity.ok().build();
    }

    public User addGuest(User user) throws SQLDataException {
        if (!userRepository.findByName("Guest-" + user.getName()).isEmpty()) {
            throw new SQLDataException(String.format("Name %s exists in users table", user.getName()));
        }

        user.setName("Guest-" + user.getName());
        user.setType(UserType.GUEST);
        user.setEmail(Utility.randomString());
        user.setPassword(Utility.randomString());
        return userRepository.save(user);
    }


    public User addUser(User user) throws SQLDataException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new SQLDataException(String.format("Email %s exists in users table", user.getEmail()));
        }
        user.setPassword(Utility.encrypt((user.getPassword())));
        user.setType(UserType.GUEST);

        sendMessage(user);

        return userRepository.save(user);
    }

    public void sendMessage(User user){
        String verifyCode = randomString();
        user.setVerifyCode(verifyCode);
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
