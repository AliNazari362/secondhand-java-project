package Repository;

import entity.Adv;
import entity.enums.AdvStatus;
import entity.enums.AdvType;
import entity.enums.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdvRepository extends JpaRepository<Adv, UUID> {

    List<Adv> findByStatus(AdvStatus status);

    List<Adv> findByCity(City city);

    List<Adv> findByAdvType(AdvType advType);

    List<Adv> findByUserId(UUID userId);

    @Query("SELECT a FROM Adv a WHERE " +
            "(:keyword IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:city IS NULL OR a.city = :city) " +
            "AND (:status IS NULL OR a.status = :status)")
    List<Adv> search(@Param("keyword") String keyword,
                     @Param("city") City city,
                     @Param("status") AdvStatus status);
}