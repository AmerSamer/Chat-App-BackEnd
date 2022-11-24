package chatApp.Utilities;

import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Utility {

    public static String guestPrefix = "Guest-";
    public static String systemEmail = "seselevtion@gmail.com";
    public static String emailContent = "Chat App Verification Code";

    //The length of the password > 6
    //At least one capital letter
    public static boolean isValidPassword(String password) {
        return password.matches(".*[A-Z].*") && password.length() >= 6;
    }

    //only letters in name
    public static boolean isValidName(String Name) {
        return Name.matches("^[ A-Za-z]+$");
    }

    public static boolean isValidEmail(String emailAddress) {
        String regexPattern = "^(.+)@(\\S+)$";

        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public static String randomString() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("_", "");
    }

    public static String encrypt(String stringToEncrypt) {
        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
        return bEncoder.encode(stringToEncrypt);
    }

    public static UserDTO userToUserDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoto(), user.getDateOfBirth(), user.getAge(), user.getUserStatus(),user.getType(), user.isMute());
    }

    public static List<UserDTO> userListToUserListDTO(List<User> users) {
        List<UserDTO> listUsers = new ArrayList<>();
        for (User user: users) {
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoto(), user.getDateOfBirth(), user.getAge(), user.getUserStatus(),user.getType(), user.isMute());
            listUsers.add(userDTO);
        }
        return listUsers;
    }

    public static UserDTO userGuestToUserDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.isMute());
    }

}
