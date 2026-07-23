package com.secondhand.repository;

import com.secondhand.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Comment} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * custom query methods for retrieving comments by advertisement or author, computing
 * aggregate rating statistics, and checking for duplicate reviews.</p>
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Returns all comments left on the advertisement with the given ID.
     *
     * @param advId the UUID of the advertisement
     * @return list of comments for that advertisement; empty list if none found
     */
    List<Comment> findByAdvId(UUID advId);

    /**
     * Returns all comments written by the user with the given ID.
     *
     * @param userId the UUID of the comment author
     * @return list of comments written by that user; empty list if none found
     */
    List<Comment> findByUserId(UUID userId);

    /**
     * Calculates the average star rating of all comments for the specified advertisement.
     *
     * @param advId the UUID of the advertisement
     * @return the average rating as a {@link Double}, or {@code null} if no comments exist
     */
    @Query("SELECT AVG(c.rate) FROM Comment c WHERE c.adv.id = :advId")
    Double getAverageRatingByAdvId(@Param("advId") UUID advId);

    /**
     * Counts the total number of comments on the specified advertisement.
     *
     * @param advId the UUID of the advertisement
     * @return the total comment count
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.adv.id = :advId")
    long countByAdvId(@Param("advId") UUID advId);

    /**
     * Checks whether a user has already submitted a comment on a specific advertisement.
     * Useful for enforcing a one-comment-per-user constraint at the application level.
     *
     * @param userId the UUID of the user
     * @param advId  the UUID of the advertisement
     * @return {@code true} if a comment from that user on that advertisement exists, {@code false} otherwise
     */
    boolean existsByUserIdAndAdvId(UUID userId, UUID advId);
}