package chatApp.utilities;

public class LoggerMessages {

    public static String beforeLoginAsGuest = "Try to login as guest to the system";
    public static String beforeActivateEmail = "Try to activate email";
    public static String beforeSendMessageInMain = "Try to send message in main chat room";
    public static String beforeSendPrivateMessage = "Try to send private message";
    public static String beforeGettingMainRoomMessages =  "Try to get main chat room messages";
    public static String beforeGettingPrivateRoomMessages =  "Try to get private chat room messages";
    public static String beforeDownloadingPrivateRoom =  "Try to download specific private chat room";
    public static String beforeDownloadingMainRoom =  "Try to download main chat room from specific time";
    public static String beforeGettingAllUsers = "Try to get all users to display in the frontend";
    public static String beforeLogout = "User try to logout in the system";
    public static String beforeMuteUnmute = "Try to mute / unmute user";
    public static String beforeUpdateStatus = "Try to changed the status of the user to ONLINE/AWAY";



    public static String beforeAnAction(String email, String action) {
        return String.format("Try to "+ action + " " + email + " to the system");
    }


}
