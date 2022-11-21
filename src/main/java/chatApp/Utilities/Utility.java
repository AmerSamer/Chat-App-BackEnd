package chatApp.Utilities;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public class Utility {
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

}
