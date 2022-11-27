package chatApp.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;
import java.time.LocalDate;

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
    private String content;
    @Column(nullable = false, length = 91)
    private String roomId;

    @Column(name = "issue_date")
    private LocalDate issueDate;


    public Message() {
    }

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.issueDate = LocalDate.now();
    }
    public Message(String sender, String content, String receiver, String roomId) {
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
        this.receiver = receiver;
        this.issueDate = LocalDate.now();
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

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public static class HelloMessage {

        private String name;

        public HelloMessage() {
        }

        public HelloMessage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }


        @Override
        public String toString() {
            return "HelloMessage{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

}