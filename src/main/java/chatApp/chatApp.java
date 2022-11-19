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
}
