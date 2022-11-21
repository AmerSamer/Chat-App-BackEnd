package chatApp.service;

import chatApp.entities.User;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.util.List;
import static chatApp.Utilities.Utility.randomString;


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
        user.setType(UserType.GUEST);

        sendMessage(user);

        return userRepository.save(user);
    }

    public User addGuest(User user) throws SQLDataException {
        if (!userRepository.findByName("Guest-" + user.getName()).isEmpty()) {
            throw new SQLDataException(String.format("Name %s exists in users table", user.getName()));
        }


        user.setName("Guest-" + user.getName());
        user.setType(UserType.GUEST);
        user.setEmail(randomString());
        user.setPassword(randomString());
        return userRepository.save(user);
    }



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

    public void sendMessage(User user){
        String verifyCode = randomString();
        user.setVerifyCode(verifyCode);
        user.setIssueDate(LocalDate.now());
        String from = "seselevtion@gmail.com";
        String to = user.getEmail();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Chat App Verification Code");
        message.setText(verifyCode);
        mailSender.send(message);
    }

    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
