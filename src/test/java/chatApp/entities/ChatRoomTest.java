package chatApp.entities;

import chatApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class ChatRoomTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TestEntityManager testEntityManager;
//
//    @Test
//    public void createNewChatRoomFromExistingUsers() {
//            User user = userRepo.findByEmail("shai@gmail.com");
//            User userTwo = userRepo.findByEmail("hen@gmail.com");
//            ArrayList<User> users = new ArrayList<>();
//            users.add(user);
//            users.add(userTwo);
//            Message message = new Message(user.getName(), " hello message");
//            Message messageTwo = new Message(userTwo.getName(), " hi to you ");
//            List<Message> messages = new ArrayList<>();
//            messages.add(message);
//            messages.add(messageTwo);
//
//            ChatRoom newChat = new ChatRoom(users,messages);
//            chatRoomRepo.save(newChat);
//    }
//
//    @Test
//    public void updateNewMessagesToExistingChatRoom() {
//        User user = userRepo.findByEmail("shai@gmail.com");
//        User userTwo = userRepo.findByEmail("hen@gmail.com");
//        ArrayList<User> users = new ArrayList<>();
//        users.add(user);
//        users.add(userTwo);
//        List<ChatRoom> chats = chatRoomRepo.getByParticipantsIn(users);
//        ChatRoom currChat = chats.get(0);
//        for (ChatRoom chat:chats) {
//            if(chat.getParticipants().equals(users))
//                currChat = chat;
//        }
//        Message message = new Message(user.getName(), "  ok");
//        Message messageTwo = new Message(userTwo.getName(), " nook ");
//        List<Message> messages = currChat.getMessages();
//        messages.add(message);
//        messages.add(messageTwo);
//
//        chatRoomRepo.save(currChat);
//    }

    @Test
    void setParticipants() {
    }

    @Test
    void setMessages() {
    }
}