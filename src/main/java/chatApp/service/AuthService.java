package chatApp.service;

import chatApp.entities.User;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

public class AuthService {
    @Autowired
    private UserRepository userRepository;
    private Map<String, String> usersTokens;
    private static final int TOKEN_LENGTH = 10;

    public String login(String email, String password) {
        String token = isValidCredentials(email, password) ? generateToken() : null;

        if (token != null) {
            this.usersTokens.put(email, token);
        }

        return token;
    }

    private boolean isValidCredentials(String email, String password) {
        Optional<User> user = userRepository.getUserByEmail(email);

        return user.filter(value -> value.getPassword().compareTo(password) == 0).isPresent();

    }
    private static String generateToken() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder(TOKEN_LENGTH);

        for(int i = 0; i < TOKEN_LENGTH; ++i) {
            int index = (int)((double)AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    public boolean register(User user) {
        if (isExistingEmail(user.getEmail())) {
            throw new IllegalArgumentException("This email address already exists!");
        }

       // User user = User.createUser(email, name, password);
        this.userRepository.addUser(user);

        return true;
    }

}
