package chatApp.entities;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
@Entity
@Table(name = "chatRoom")
@DynamicUpdate
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.FALSE)
    @Column(nullable = false, unique = true)
    private List<User> participants;

    @Embedded
    @ManyToMany(cascade = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.FALSE)
    @Column(nullable = false, unique = true)
    private List<Message> messages;


    public ChatRoom() {
    }
    public ChatRoom(List<User> participants, List<Message> messages) {
        this.participants = participants;
        this.messages = messages;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Long getId() {
        return id;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
