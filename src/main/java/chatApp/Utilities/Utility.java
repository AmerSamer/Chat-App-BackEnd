package chatApp.Utilities;

import java.util.regex.Pattern;

public class Utility {
    public static boolean isValidPassword(String password) {
        return password.matches(".*[A-Z].*") && password.length() >= 6;
    }

    public static boolean isValidName(String Name) {
        return Name.matches("^[ A-Za-z]+$");
    }

    public static boolean isValidEmail(String emailAddress) {
        String regexPattern = "^(.+)@(\\S+)$";

        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
