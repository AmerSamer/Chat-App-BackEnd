package chatApp.Utilities;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public static String encrypt(String stringToEncrypt){
        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
        return bEncoder.encode(stringToEncrypt);
    }

}
