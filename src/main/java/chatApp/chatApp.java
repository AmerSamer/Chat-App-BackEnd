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
//    CommandLineRunner commandLineRunner(UserRepository userRepo){
//        return args -> {
//            User user = new User("shai", "shai@gmail.com", "1234");
//            User userTwo = new User("tal", "tal@gmail.com", "4321");
//
//            userRepo.save(user);
//            userRepo.save(userTwo);
//        };
//    }

//    @Bean
//    CommandLineRunner commandLineRunner(MessageRepository messageRepo){
//        return args -> {
//         User user = new User("shai", "shai@gmail.com", "1234");
//         User userTwo = new User("tal", "tal@gmail.com", "4321");
//         Message message = new Message(user.getName(), " blah blah blah");
//         Message messageTwo = new Message(userTwo.getName(), " no ");
//         messageRepo.save(message);
//         messageRepo.save(messageTwo);
//        };
//    }
//
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

    @Bean
    CommandLineRunner commandLineRunner(ChatRoomRepository chatRoomRepo){
        return args -> {
            ChatRoom cr = chatRoomRepo.findById(1L).get();
            List<User> users = cr.getParticipants();
            Message message = new Message(users.get(0).getName(), " new new Message blah");
            List<Message> messages = cr.getMessages();
            messages.add(message);
            cr.setMessages(messages);
            chatRoomRepo.save(cr);
        };
    }
}
