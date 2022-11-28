package chatApp.repository;

import chatApp.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {

    List<Message> findByRoomId(String roomId);

    List<Message> findByColumnDateBetween(LocalDateTime to, LocalDateTime from);


}

