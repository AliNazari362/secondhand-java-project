package com.secondhand.repository;

import com.secondhand.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Chatroom} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * custom query methods for looking up chatrooms by participant or by the combination
 * of user and advertisement.</p>
 */
@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, UUID> {

    /**
     * Returns all chatrooms owned by (initiated by) the user with the given ID.
     *
     * @param userId the UUID of the chatroom owner (buyer)
     * @return list of chatrooms for that user; empty list if none found
     */
    List<Chatroom> findByUserId(UUID userId);

    /**
     * Finds the chatroom initiated by a specific user for a specific advertisement,
     * using the derived-query strategy on the stored {@code user_id} column.
     *
     * @param userId the UUID of the chatroom owner (buyer)
     * @param advId  the UUID of the advertisement
     * @return an {@link Optional} containing the chatroom if it exists, or empty if not
     */
    Optional<Chatroom> findByUserIdAndAdvId(UUID userId, UUID advId);

    /**
     * Finds the chatroom for a specific user and advertisement using an explicit JPQL query
     * that navigates the {@link com.secondhand.entity.User#rooms} collection.
     *
     * <p>This is an alternative to {@link #findByUserIdAndAdvId(UUID, UUID)} and resolves
     * the chatroom via the bidirectional relationship rather than the stored foreign key column.</p>
     *
     * @param userId the UUID of the user
     * @param advId  the UUID of the advertisement
     * @return an {@link Optional} containing the chatroom if it exists, or empty if not
     */
    @Query("SELECT c FROM Chatroom c WHERE c.adv.id = :advId AND c.id IN (SELECT r.id FROM User u JOIN u.rooms r WHERE u.id = :userId)")
    Optional<Chatroom> findByUserIdAndAdvId2(@Param("userId") UUID userId, @Param("advId") UUID advId);

    /**
     * Returns all chatrooms in which the given user participates — either as the buyer
     * (chatroom owner) or as the seller (advertisement owner).
     *
     * @param userId the UUID of the participant to search for
     * @return list of chatrooms involving that user; empty list if none found
     */
    @Query("SELECT c FROM Chatroom c WHERE c.userId = :userId OR c.adv.user.id = :userId")
    List<Chatroom> findByParticipantId(@Param("userId") UUID userId);
}