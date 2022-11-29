package chatApp.repository;

import chatApp.entities.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomId(String roomId);

    List<Message> findByRoomIdAndIssueDateTimeBetweenAndIssueDateBetween(String roomId, String toTime, String fromTime, String to, String from);

    List<Message> findByRoomIdAndIssueDateBetween(String roomId, String to, String from);

    List<Message> findByRoomId(String roomId, Pageable pageable);
}

