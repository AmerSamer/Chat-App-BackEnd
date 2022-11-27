package chatApp.Utilities;

import chatApp.controller.UserController;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import org.apache.log4j.chainsaw.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static Logger logger = LogManager.getLogger(Utility.class.getName());


    public static List<String> permissionPathsForAll = new ArrayList<>(List.of("/sign", "/ws", "/chat"));
    public static List<String> permissionPathsForGuest = new ArrayList<>(List.of("/logout", "update/status"));
    public static List<String> noPermissionsPathsForRegistered = new ArrayList<>(List.of("update/mute"));

    //The length of the password > 6
    //At least one capital letter
    public static boolean isValidPassword(String password) {
        logger.debug("Check valid password");
        if(password != null){
            return password.matches(".*[A-Z].*") && password.length() >= 6;
        }
        return false;
    }

    //only letters in name
    public static boolean isValidName(String name) {
        logger.debug("Check valid name");
        if(name != null) {
            return name.matches("^[ A-Za-z]+$");
        }
        return false;
    }

    public static boolean isValidEmail(String emailAddress) {
        logger.debug("Check valid email");
        if(emailAddress != null) {
            String regexPattern = "^(.+)@(\\S+)$";

            return Pattern.compile(regexPattern)
                    .matcher(emailAddress)
                    .matches();
        }
        return false;
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
