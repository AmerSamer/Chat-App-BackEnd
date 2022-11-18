package chatApp.repository;

import chatApp.entities.ChatRoom;
import chatApp.entities.Message;
import chatApp.entities.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
}
