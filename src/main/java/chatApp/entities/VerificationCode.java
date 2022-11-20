package chatApp.entities;

import chatApp.Utilities.Utility;

import javax.persistence.*;
import java.time.LocalDate;

@Embeddable
public class VerificationCode {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    private String verifyCode;

    private LocalDate issueDate;

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public static VerificationCode createVerificationCode() {
        VerificationCode vc = new VerificationCode();
        vc.setIssueDate(LocalDate.now());
        vc.setVerifyCode(Utility.randomVerificationCode());
        return vc;
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
}
