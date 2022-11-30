package chatApp.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;
import java.time.LocalDateTime;

import static chatApp.Utilities.Utility.getDateNow;
import static chatApp.Utilities.Utility.getDateTimeNow;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String receiver;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private String roomId;
    @Column(name = "issue_date")
    private String issueDate;

    @Column(name = "issue_date_time")
    private String issueDateTime;


    private Message() {
    }
    public Message(String sender, String content, String receiver, String roomId) {
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
        this.receiver = receiver;
        this.issueDate = getDateNow();
        this.issueDateTime = getDateTimeNow();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueDateTime() {
        return issueDateTime;
    }

    public void setIssueDateTime(String issueDateTime) {
        this.issueDateTime = issueDateTime;
    }

}