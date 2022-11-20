package chatApp.service;

import chatApp.entities.User;
import chatApp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Adds a user to the database if it has a unique email
     * @param user - the user's data
     * @return a saved user with it's generated id
     * @throws SQLDataException when the provided email already exists
     */
    public User addUser(User user) throws SQLDataException {
        if(userRepository.findByEmail(user.getEmail())!=null){
            throw new SQLDataException(String.format("Email %s exists in users table", user.getEmail()));
        }

//        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
//        String encoderPassword = bEncoder.encode(user.getPassword());
//        user.setPassword(encoderPassword);
        return userRepository.save(user);
    }

    public ResponseEntity<String> login(User user) throws SQLDataException {
        User checkUser = userRepository.findByEmail(user.getEmail());
        if(checkUser == null){
            throw new SQLDataException(String.format("Email %s doesn't exists in users table", user.getEmail()));
        }
        else if(!checkUser.getPassword().equals(user.getPassword())){
            throw new SQLDataException(String.format("Password %s doesn't match to email", user.getEmail()));
        }

        return ResponseEntity.ok().body("token");
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken newUserToken = new VerificationToken(token, user);
        userRepository.save(newUserToken);
    }
}
