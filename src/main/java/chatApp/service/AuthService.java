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

import static chatApp.Utilities.ExceptionHandler.*;
import static chatApp.Utilities.Utility.*;


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
            throw new SQLDataException(emailNotExistsMessage(user.getEmail()));
        }
        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
        if (!bEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new SQLDataException(passwordDosentMatchMessage(user.getPassword()));
        }
        return ResponseEntity.ok().build();
    }

    public User addGuest(User user) throws SQLDataException {
        if (!userRepository.findByName(guestPrefix + user.getName()).isEmpty()) {
            throw new SQLDataException(guestNameExistsMessage(user.getName()));
        }

        user.setName(guestPrefix + user.getName());
        user.setType(UserType.GUEST);
        user.setEmail(Utility.randomString());
        user.setPassword(Utility.randomString());
        return userRepository.save(user);
    }


    public User addUser(User user) throws SQLDataException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new SQLDataException(emailExistsInSystemMessage(user.getEmail()));
        }
        user.setPassword(encrypt((user.getPassword())));
        user.setType(UserType.GUEST);
        sendMessage(user);

        return userRepository.save(user);
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
