package Repository;

import entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByAdvId(UUID advId);

    List<Comment> findByUserId(UUID userId);

    @Query("SELECT AVG(c.rate) FROM Comment c WHERE c.adv.id = :advId")
    Double getAverageRatingByAdvId(@Param("advId") UUID advId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.adv.id = :advId")
    long countByAdvId(@Param("advId") UUID advId);

    boolean existsByUserIdAndAdvId(UUID userId, UUID advId);
}