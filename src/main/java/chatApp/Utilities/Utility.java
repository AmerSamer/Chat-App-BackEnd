package chatApp.Utilities;

import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Utility {

    public static String guestPrefix = "Guest-";
    public static String systemEmail = "seselevtion@gmail.com";
    public static String emailContent = "Chat App Verification Code";

    public static List<String> permissionPathsForAll = new ArrayList<>(List.of("/sign", "/ws", "/chat"));
    public static List<String> permissionPathsForGuest = new ArrayList<>(List.of("/user/logout"));


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
            return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoto(), user.getDateOfBirth(), user.getAge(), user.getUserStatus(), user.getType(), user.isMute());
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
        return new UserDTO(user.getId(), user.getName(),user.getEmail(), user.isMute());
    }

    public static int calcAge (LocalDate dateOfBirth){
        return LocalDate.now().minusYears(dateOfBirth.getYear()).getYear();
    }

    public static List<String> paths(){
        List<String> paths = new ArrayList<>();
        paths.add("/sign");
        paths.add("/ws");
        paths.add("/chat");
        return paths;
    }

}
