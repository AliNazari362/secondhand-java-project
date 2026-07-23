package com.secondhand.repository;

import com.secondhand.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Option} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * custom query methods for retrieving and bulk-deleting options belonging to a specific
 * advertisement.</p>
 */
@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    /**
     * Returns all key-value options attached to the advertisement with the given ID.
     *
     * @param advId the UUID of the advertisement
     * @return list of options for that advertisement; empty list if none found
     */
    @Query("SELECT o FROM Option o WHERE o.adv.id = :advId")
    List<Option> findByAdvId(@Param("advId") UUID advId);

    /**
     * Deletes all options belonging to the advertisement with the given ID.
     * Useful for bulk-replacing all options when an advertisement is updated.
     *
     * @param advId the UUID of the advertisement whose options should be removed
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Option o WHERE o.adv.id = :advId")
    void deleteByAdvId(@Param("advId") UUID advId);
}