package chatApp.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, length = 20)
    private String name;
    @Column(nullable = false, unique = true, length = 45)
    private String email;
    @Column(nullable = false, length = 64)
    private String password;
    @Column()
    private String photo;
    @Column()
    private LocalDate dateOfBirth;
    @Column()
    private int age;
    @Column()
    private UserType type;
    @Column()
    private UserStatuses userStatus;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "verification_code")
    private String verifyCode;
    @Column(name = "issue_date")
    private LocalDate issueDate;

    public User() {
        super();
        this.enabled = false;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = false;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public UserStatuses getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatuses userStatus) {
        this.userStatus = userStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (Age != user.Age) return false;
        if (enabled != user.enabled) return false;
        if (!Objects.equals(id, user.id)) return false;
        if (!Objects.equals(name, user.name)) return false;
        if (!Objects.equals(email, user.email)) return false;
        if (!Objects.equals(password, user.password)) return false;
        if (!Objects.equals(Photo, user.Photo)) return false;
        if (!Objects.equals(DateOfBirth, user.DateOfBirth)) return false;
        if (type != user.type) return false;
        if (userStatus != user.userStatus) return false;
        if (!Objects.equals(verifyCode, user.verifyCode)) return false;
        return Objects.equals(issueDate, user.issueDate);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (Photo != null ? Photo.hashCode() : 0);
        result = 31 * result + (DateOfBirth != null ? DateOfBirth.hashCode() : 0);
        result = 31 * result + Age;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (userStatus != null ? userStatus.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (verifyCode != null ? verifyCode.hashCode() : 0);
        result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", userStatus=" + userStatus +
                ", enabled=" + enabled +
                ", verifyCode='" + verifyCode + '\'' +
                ", issueDate=" + issueDate +
                ", photo='" + photo + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", age=" + age +
                '}';
    }
}
