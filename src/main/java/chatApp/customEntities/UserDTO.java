package chatApp.customEntities;

import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;

import java.time.LocalDate;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String photo;
    private LocalDate dateOfBirth;
    private int age;
    private UserStatuses userStatus;
    private UserType userType;
    private boolean isMute;

    private String nickname;
    private String description;

    public UserDTO(Long id, String name, String email, String photo, LocalDate dateOfBirth, int age, UserStatuses userStatus, UserType userType, boolean isMute, String nickname, String description) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.userStatus = userStatus;
        this.userType = userType;
        this.isMute = isMute;
        this.nickname = nickname;
        this.description = description;
    }

    public UserDTO(Long id, String name, String email, String photo, LocalDate dateOfBirth, int age, UserStatuses userStatus, UserType userType, boolean isMute, String nickname) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.userStatus = userStatus;
        this.userType = userType;
        this.isMute = isMute;
        this.nickname = nickname;
    }

    public UserDTO(Long id, String name, String email, boolean isMute, String nickname) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isMute = isMute;
        this.nickname = nickname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public UserStatuses getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatuses userStatus) {
        this.userStatus = userStatus;
    }
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", age=" + age +
                ", userStatus=" + userStatus +
                ", userType=" + userType +
                ", isMute=" + isMute +
                ", nickname='" + nickname + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
