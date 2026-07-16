package Repository;

import Entity.Message;
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

    // ✅ استفاده از Query به‌جای Derived Query
    @Query("SELECT m FROM Message m WHERE m.chatroom.id = :chatroomId ORDER BY m.date ASC")
    List<Message> findByChatroomIdOrderByDateAsc(@Param("chatroomId") UUID chatroomId);

    @Query("SELECT m FROM Message m WHERE m.chatroom.id = :chatroomId ORDER BY m.date DESC")
    List<Message> findByChatroomIdOrderByDateDesc(@Param("chatroomId") UUID chatroomId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatroom.id = :chatroomId AND m.seen = false")
    long countByChatroomIdAndSeenFalse(@Param("chatroomId") UUID chatroomId);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.seen = true WHERE m.chatroom.id = :chatroomId AND m.sender.id != :userId")
    void markAllAsSeen(@Param("chatroomId") UUID chatroomId, @Param("userId") UUID userId);
}