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

    public UserDTO(Long id, String name, String email, String photo, LocalDate dateOfBirth, int age, UserStatuses userStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.userStatus = userStatus;
    }

    public UserDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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
                '}';
    }
}
