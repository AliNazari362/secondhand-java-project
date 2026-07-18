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

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("SELECT i FROM Image i WHERE i.adv.id = :advId")
    List<Image> findByAdvId(@Param("advId") UUID advId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Image i WHERE i.adv.id = :advId")
    void deleteByAdvId(@Param("advId") UUID advId);
}