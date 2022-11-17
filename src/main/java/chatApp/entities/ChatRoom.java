package chatApp.entities;

import javax.persistence.*;
import java.util.List;
@Entity
@Table(name = "chatRoom")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private List<User> participants;
    private List<Message> messages;

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
