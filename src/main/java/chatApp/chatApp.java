package chatApp;

import java.util.List;
import chatApp.entities.ChatRoom;
import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.repository.ChatRoomRepository;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class chatApp {
    public static void main(String[] args) {
        SpringApplication.run(chatApp.class, args);
    }

//    @Bean
//    CommandLineRunner commandLineRunner(ChatRoomRepository chatRoomRepo){
//        return args -> {
//            User user = new User("shai", "shai@gmail.com", "1234");
//            User userTwo = new User("tal", "tal@gmail.com", "4321");
//
//
//            List<User> users = new ArrayList<>();
//            users.add(user);
//            users.add(userTwo);
//            Message message = new Message(user.getName(), " blah blah blah");
//            Message messageTwo = new Message(userTwo.getName(), " no ");
//            List<Message> messages = new ArrayList<>();
//            messages.add(message);
//            messages.add(messageTwo);
//            ChatRoom chat = new ChatRoom(users,messages);
//            chatRoomRepo.save(chat);
//        };
//    }
}
