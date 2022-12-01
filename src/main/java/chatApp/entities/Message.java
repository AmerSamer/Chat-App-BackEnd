package chatApp.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static chatApp.utilities.Utility.getLocalDateTimeNow;

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
    private LocalDateTime issueDate;

    @Column(name = "issue_date_time")
    private long issueDateEpoch;


    private Message() {
    }

    public Message(String sender, String content, String receiver, String roomId) {
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
        this.receiver = receiver;
        this.issueDate = getLocalDateTimeNow();
        this.issueDateEpoch = this.issueDate.toEpochSecond(ZoneOffset.of("Z"));
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

    public LocalDateTime getIssueDate() {
        return LocalDateTime.of(issueDate.getYear(), issueDate.getMonthValue(), issueDate.getDayOfMonth(), issueDate.getHour(), issueDate.getMinute(), issueDate.getSecond(), issueDate.getNano());
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = LocalDateTime.of(issueDate.getYear(), issueDate.getMonthValue(), issueDate.getDayOfMonth(), issueDate.getHour(), issueDate.getMinute(), issueDate.getSecond(), issueDate.getNano());
    }

    public long getIssueDateEpoch() {
        return issueDateEpoch;
    }

    public void setIssueDateEpoch(long issueDateEpoch) {
        this.issueDateEpoch = issueDateEpoch;
    }
}