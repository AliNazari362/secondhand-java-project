package Repository;

import entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatroomIdOrderByDateAsc(UUID chatroomId);

    List<Message> findByChatroomIdOrderByDateDesc(UUID chatroomId);

    long countByChatroomIdAndSeenFalse(UUID chatroomId);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.seen = true WHERE m.chatroomId = :chatroomId AND m.sender.id != :userId")
    void markAllAsSeen(@Param("chatroomId") UUID chatroomId, @Param("userId") UUID userId);
}