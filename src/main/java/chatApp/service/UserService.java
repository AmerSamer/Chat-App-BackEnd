package chatApp.service;

import chatApp.entities.User;
import chatApp.entities.VerificationCode;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public UserService() {
    }


    /**
     * Adds a user to the database if it has a unique email
     *
     * @param user - the user's data
     * @return a saved user with it's generated id
     * @throws SQLDataException when the provided email already exists
     */
    public User addUser(User user) throws SQLDataException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new SQLDataException(String.format("Email %s exists in users table", user.getEmail()));
        }
        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
        String encoderPassword = bEncoder.encode(user.getPassword());
        user.setPassword(encoderPassword);

        sendMessage(user);

        return userRepository.save(user);
    }

    public ResponseEntity<String> login(User user) throws SQLDataException {
        User checkUser = userRepository.findByEmail(user.getEmail());
        if (checkUser == null) {
            throw new SQLDataException(String.format("Email %s doesn't exists in users table", user.getEmail()));
        }
        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
        if (!bEncoder.matches(user.getPassword(), checkUser.getPassword())) {
            throw new SQLDataException(String.format("Password %s doesn't match to email", user.getEmail()));
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<String> verifyEmail(User user, String verifyCode) {
        if(user.isEnabled()){
            throw new IllegalArgumentException("User already activate");
        }
        else if(user.getVerificationCode().getIssueDate().isAfter(user.getExpirationDate())){
            sendMessage(user);
            throw new IllegalArgumentException("More than 24 hours passed from last verification code, new verification code has been sent");
        }
        else if(!user.getVerificationCode().getVerifyCode().equals(verifyCode)){
            throw new IllegalArgumentException("Verification code doesn't match");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        userRepository.deleteById(user.getId());
        userRepository.save(user);
        return ResponseEntity.ok("success, email verified");
    }

    public void sendMessage(User user){
        VerificationCode vc = VerificationCode.createVerificationCode();
        user.setVerificationCode(vc);
        String from = "seselevtion@gmail.com";
        String to = user.getEmail();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Chat App Verification Code");
        message.setText(user.getVerificationCode().getVerifyCode());
        mailSender.send(message);
    }
}
