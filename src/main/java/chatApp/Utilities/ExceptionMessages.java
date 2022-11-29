package chatApp.Utilities;

public class ExceptionMessages {

    public static String invalidEmailMessage = "Invalid Email, Please try again. Example: sample@test.com";
    public static String invalidNameMessage = "Invalid Name, Please try again without numbers. Example: Some Name";
    public static String invalidPasswordMessage = "Invalid Password, Please try again with at least 1 capital letter and minimum 6 letters. Example: passWord";
    public static String loginFailedMessage = "Email or Password are wrong";
    public static String activationEmailFailedMessage = "Email activation failed, Couldn't activate email";
    public static String updateUserFailedMessage = "Update user failed. Please try again";
    public static String loginAsGuestFailedMessage = "Guest-Name already exists in the system, Please try again.";

    public static String logoutUserFailedMessage = "Logout user failed. Please try again";

    public static String verificationCodeNotMatch = "Verification code doesn't match. Please try again";
    public static String tokenSessionExpired = "Token session expired, please log-in again.";

    public static String userIsMutedMessage = "You are muted, can't send messages";



    public static String emailNotExistsMessage(String email) {
        return String.format("Email %s doesn't exists in users table", email);
    }
    public static String passwordDosentMatchMessage(String password) {
        return String.format("Password %s doesn't match to email", password);
    }

    public static String guestNameExistsMessage(String name) {
        return String.format("Name %s exists in users table", name);
    }

    public static String emailExistsInSystemMessage(String email) {
        return String.format("Email %s already exists in users table", email);
    }

    public static String emailAlreadyActivatedMessage(String email) {
        return String.format("User already activated with the following email: %s", email);
    }

    public static String emailIssueTokenPassedMessage(String issueDate) {
        return String.format( "More than 24 hours passed from last verification code issue at %s, new verification code has been sent", issueDate);
    }


}
