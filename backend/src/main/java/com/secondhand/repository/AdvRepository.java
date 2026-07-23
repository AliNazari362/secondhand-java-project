package com.secondhand.repository;

import com.secondhand.entity.Adv;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Adv} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * custom query methods for filtering advertisements by status, city, type, owner,
 * category, price range, and a full-text keyword search with sorting capabilities.</p>
 *
 * <p>The repository uses JPQL with Hibernate and is compatible with SQLite via the
 * {@code hibernate-community-dialects} library.</p>
 */
@Repository
public interface AdvRepository extends JpaRepository<Adv, UUID> {

    /**
     * Returns all advertisements with the given lifecycle status.
     *
     * @param status the {@link AdvStatus} to filter by
     * @return list of matching advertisements
     */
    List<Adv> findByStatus(AdvStatus status);

    /**
     * Returns all advertisements located in the specified city.
     *
     * @param city the {@link City} to filter by
     * @return list of matching advertisements
     */
    List<Adv> findByCity(City city);

    /**
     * Returns all advertisements of the given type (PRODUCT or SERVICE).
     *
     * @param advType the {@link AdvType} discriminator to filter by
     * @return list of matching advertisements
     */
    List<Adv> findByAdvType(AdvType advType);

    /**
     * Returns all advertisements posted by the user with the given ID.
     *
     * @param userId the UUID of the advertisement owner
     * @return list of advertisements belonging to that user
     */
    List<Adv> findByUserId(UUID userId);

    /**
     * Searches advertisements by keyword, city, status, category, and price range,
     * excluding DELETED and REJECTED ones, with dynamic sorting.
     *
     * <p>All parameters are optional. When a parameter is {@code null} it is ignored,
     * making this a flexible multi-criteria search query.</p>
     *
     * <p><strong>Sorting options:</strong></p>
     * <ul>
     *   <li>{@code newest} - orders by creation date descending (most recent first)</li>
     *   <li>{@code oldest} - orders by creation date ascending (oldest first)</li>
     *   <li>{@code priceAsc} - orders by product price ascending (lowest first)</li>
     *   <li>{@code priceDesc} - orders by product price descending (highest first)</li>
     *   <li>{@code ratingDesc} - orders by average rating descending (highest rated first)</li>
     * </ul>
     *
     * <p><strong>Price Range Filtering:</strong> The query uses subqueries to check
     * the price of products. For service advertisements (which have no price),
     * the price conditions are ignored (treated as NULL).</p>
     *
     * @param keyword     optional text to match against the title or description (case-insensitive)
     * @param city        optional city to restrict results to
     * @param status      optional lifecycle status to restrict results to
     * @param categoryIds optional list of category IDs to restrict results to (including subcategories)
     * @param sortBy      sorting criterion (default: "newest")
     * @param minPrice    optional minimum price filter (inclusive, only for products)
     * @param maxPrice    optional maximum price filter (inclusive, only for products)
     * @return list of matching advertisements; empty list if none found
     */
    @Query("SELECT a FROM Adv a WHERE " +
            "(:keyword IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:city IS NULL OR a.city = :city) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:categoryIds IS NULL OR a.category.id IN :categoryIds) " +
            "AND (:minPrice IS NULL OR (SELECT p.price FROM Product p WHERE p.id = a.id) >= :minPrice) " +
            "AND (:maxPrice IS NULL OR (SELECT p.price FROM Product p WHERE p.id = a.id) <= :maxPrice) " +
            "AND a.status != 'DELETED' AND a.status != 'REJECTED' " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'newest' THEN a.creationDate END DESC, " +
            "CASE WHEN :sortBy = 'oldest' THEN a.creationDate END ASC, " +
            "CASE WHEN :sortBy = 'priceAsc' THEN (SELECT p.price FROM Product p WHERE p.id = a.id) END ASC, " +
            "CASE WHEN :sortBy = 'priceDesc' THEN (SELECT p.price FROM Product p WHERE p.id = a.id) END DESC, " +
            "CASE WHEN :sortBy = 'ratingDesc' THEN (SELECT AVG(c.rate) FROM Comment c WHERE c.adv.id = a.id) END DESC")
    List<Adv> search(@Param("keyword") String keyword,
                     @Param("city") City city,
                     @Param("status") AdvStatus status,
                     @Param("categoryIds") List<Long> categoryIds,
                     @Param("sortBy") String sortBy,
                     @Param("minPrice") BigDecimal minPrice,
                     @Param("maxPrice") BigDecimal maxPrice);
}