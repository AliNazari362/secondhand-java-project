package com.secondhand.repository;

import com.secondhand.entity.Service;
import com.secondhand.entity.Service.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Service} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * derived query methods for filtering service advertisements by sub-category, billing
 * type, and cost range.</p>
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    /**
     * Returns all service advertisements belonging to the specified free-text sub-category.
     *
     * @param specialCategory the sub-category string to match (exact match)
     * @return list of matching services; empty list if none found
     */
    List<Service> findBySpecialCategory(String specialCategory);

    /**
     * Returns all service advertisements with the specified billing type.
     *
     * @param typeOfPart the {@link ServiceType} to filter by (e.g., HOURLY, FIXED)
     * @return list of matching services; empty list if none found
     */
    List<Service> findByTypeOfPart(ServiceType typeOfPart);

    /**
     * Returns all service advertisements whose cost per billing unit falls within the
     * given inclusive range.
     *
     * @param min the minimum cost per unit (inclusive)
     * @param max the maximum cost per unit (inclusive)
     * @return list of services within the cost range; empty list if none found
     */
    List<Service> findByCostOfPartBetween(BigDecimal min, BigDecimal max);
}
