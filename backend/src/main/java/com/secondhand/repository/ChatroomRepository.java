package com.secondhand.repository;

import com.secondhand.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, UUID> {

    List<Chatroom> findByUserId(UUID userId);

    Optional<Chatroom> findByUserIdAndAdvId(UUID userId, UUID advId);

    @Query("SELECT c FROM Chatroom c WHERE c.adv.id = :advId AND c.id IN (SELECT r.id FROM User u JOIN u.rooms r WHERE u.id = :userId)")
    Optional<Chatroom> findByUserIdAndAdvId2(@Param("userId") UUID userId, @Param("advId") UUID advId);

    @Query("SELECT c FROM Chatroom c WHERE c.userId = :userId OR c.adv.user.id = :userId")
    List<Chatroom> findByParticipantId(@Param("userId") UUID userId);
}