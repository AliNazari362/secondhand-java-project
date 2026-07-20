package com.secondhand.repository;

import com.secondhand.entity.Adv;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Adv} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * custom query methods for filtering advertisements by status, city, type, owner,
 * category, and a full-text keyword search.</p>
 *
 * <p>The repository uses JPQL with Hibernate and is compatible with SQLite via the
 * {@code hibernate-community-dialects} library.</p>
 */
@Repository
public interface AdvRepository extends JpaRepository<Adv, UUID> {

    /**
     * Returns all advertisements with the given lifecycle status.
     *
     * @param status the {@link AdvStatus} to filter by (e.g., PENDING, ACTIVE)
     * @return list of matching advertisements; empty list if none found
     */
    List<Adv> findByStatus(AdvStatus status);

    /**
     * Returns all advertisements located in the specified city.
     *
     * @param city the {@link City} to filter by
     * @return list of matching advertisements; empty list if none found
     */
    List<Adv> findByCity(City city);

    /**
     * Returns all advertisements of the given type (PRODUCT or SERVICE).
     *
     * @param advType the {@link AdvType} discriminator to filter by
     * @return list of matching advertisements; empty list if none found
     */
    List<Adv> findByAdvType(AdvType advType);

    /**
     * Returns all advertisements posted by the user with the given ID.
     *
     * @param userId the UUID of the advertisement owner
     * @return list of advertisements belonging to that user; empty list if none found
     */
    List<Adv> findByUserId(UUID userId);

    /**
     * Searches advertisements by keyword, city, status, and category,
     * excluding DELETED and REJECTED ones.
     *
     * <p>All parameters are optional. When a parameter is {@code null} it is ignored,
     * making this a flexible multi-criteria search query.</p>
     *
     * <p><strong>Note on compatibility:</strong> The query uses {@code CONCAT} instead of
     * the pipe operator ({@code ||}) to ensure full compatibility with SQLite and JPQL.</p>
     *
     * @param keyword    optional text to match against the title or description (case-insensitive)
     * @param city       optional city to restrict results to
     * @param status     optional lifecycle status to restrict results to
     * @param categoryId optional category ID to restrict results to (matches the new {@link Category} entity)
     * @return list of matching advertisements; empty list if none found
     */
    @Query("SELECT a FROM Adv a WHERE " +
            "(:keyword IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:city IS NULL OR a.city = :city) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:categoryId IS NULL OR a.category.id = :categoryId) " + // شرط جدید برای دسته‌بندی
            "AND a.status != 'DELETED' AND a.status != 'REJECTED'")
    List<Adv> search(@Param("keyword") String keyword,
                     @Param("city") City city,
                     @Param("status") AdvStatus status,
                     @Param("categoryId") Long categoryId); // پارامتر جدید
}