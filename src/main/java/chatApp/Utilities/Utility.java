package chatApp.Utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Utility {

    public static String guestPrefix = "Guest-";
    public static String systemEmail = "seselevtion@gmail.com";
    public static String emailContent = "Chat App Verification Code";
    private static Logger logger = LogManager.getLogger(Utility.class.getName());


    public static List<String> permissionPathsForAll = new ArrayList<>(List.of("/sign", "ws", "/mainchatroom", "/downloadmainchatroom", "/error"));
    public static List<String> permissionPathsForGuest = new ArrayList<>(List.of("/logout", "update/status", "chat/getusers", "chat/mainchatroom", "chat/downloadmainchatroom", "/topic", "/app", "/plain"));
    public static List<String> noPermissionsPathsForRegistered = new ArrayList<>(List.of("update/mute"));

    /**
     *Is valid password : check if The length of the password > 6 & At least one capital letter
     * @param password - the password
     * @return true if valid password else false
     */
    public static boolean isValidPassword(String password) {
        logger.debug("Check valid password");
        if(password != null){
            return password.matches(".*[A-Z].*") && password.length() >= 6;
        }
        return false;
    }
    /**
     *Is valid name : check if only letters in name
     * @param name - the password
     * @return true if valid name else false
     */
    public static boolean isValidName(String name) {
        logger.debug("Check valid name");
        if(name != null) {
            return name.matches("^[ A-Za-z]+$");
        }
        return false;
    }
    /**
     *Is valid email: check if syntax of email is valid
     * @param emailAddress - the password
     * @return true if valid emailAddress else false
     */
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
    /**
     *Random string: generate random string
     * @return true if valid emailAddress else false
     */
    public static String randomString() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("_", "");
    }
    /**
     *encrypt: encrypt string
     * @param stringToEncrypt - the string to encrypt
     * @return the value encrypted
     */
    public static String encrypt(String stringToEncrypt) {
        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
        return bEncoder.encode(stringToEncrypt);
    }

    /**
     *Calculate Age : calculate the age of the user
     * @return the age of the user
     */
    public static int calcAge(LocalDate dateOfBirth) {
        return LocalDate.now().minusYears(dateOfBirth.getYear()).getYear();
    }

    /**
     *Calculate LocalDateTime : calculate the current date and time
     * @return the current date and time
     */
    public static LocalDateTime getLocalDateTimeNow(){
        return LocalDateTime.now();
    }

}
