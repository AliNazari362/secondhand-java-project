package com.secondhand.repository;

import com.secondhand.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Image} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * custom query methods for retrieving and bulk-deleting images belonging to a specific
 * advertisement.</p>
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Returns all images attached to the advertisement with the given ID.
     *
     * @param advId the UUID of the advertisement
     * @return list of images for that advertisement; empty list if none found
     */
    @Query("SELECT i FROM Image i WHERE i.adv.id = :advId")
    List<Image> findByAdvId(@Param("advId") UUID advId);

    /**
     * Deletes all images belonging to the advertisement with the given ID.
     * Useful for removing all images before uploading a new set during an advertisement update.
     *
     * @param advId the UUID of the advertisement whose images should be removed
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Image i WHERE i.adv.id = :advId")
    void deleteByAdvId(@Param("advId") UUID advId);
}