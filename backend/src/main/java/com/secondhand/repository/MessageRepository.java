package com.secondhand.repository;

import com.secondhand.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Message} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * custom query methods for retrieving messages within a chatroom, counting unread messages,
 * and bulk-marking messages as seen.</p>
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Returns all messages in the specified chatroom, ordered by date ascending (oldest first).
     *
     * @param chatroomId the UUID of the chatroom
     * @return list of messages sorted from oldest to newest; empty list if none found
     */
    @Query("SELECT m FROM Message m WHERE m.chatroom.id = :chatroomId ORDER BY m.date ASC")
    List<Message> findByChatroomIdOrderByDateAsc(@Param("chatroomId") UUID chatroomId);

    /**
     * Returns all messages in the specified chatroom, ordered by date descending (newest first).
     *
     * @param chatroomId the UUID of the chatroom
     * @return list of messages sorted from newest to oldest; empty list if none found
     */
    @Query("SELECT m FROM Message m WHERE m.chatroom.id = :chatroomId ORDER BY m.date DESC")
    List<Message> findByChatroomIdOrderByDateDesc(@Param("chatroomId") UUID chatroomId);

    /**
     * Counts the number of unread (not yet seen) messages in the specified chatroom.
     *
     * @param chatroomId the UUID of the chatroom
     * @return the count of messages with {@code seen = false}
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatroom.id = :chatroomId AND m.seen = false")
    long countByChatroomIdAndSeenFalse(@Param("chatroomId") UUID chatroomId);

    /**
     * Marks all messages in the specified chatroom as seen, excluding messages sent by the
     * given user (a user should not mark their own outgoing messages as seen).
     *
     * @param chatroomId the UUID of the chatroom whose messages should be marked as seen
     * @param userId     the UUID of the user whose own messages are excluded from the update
     */
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.seen = true WHERE m.chatroom.id = :chatroomId AND m.sender.id != :userId")
    void markAllAsSeen(@Param("chatroomId") UUID chatroomId, @Param("userId") UUID userId);
}