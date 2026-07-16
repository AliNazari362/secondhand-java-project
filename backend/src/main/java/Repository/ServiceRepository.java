package Repository;

import Entity.Service;
import Entity.Service.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    List<Service> findBySpecialCategory(String specialCategory);

    List<Service> findByTypeOfPart(ServiceType typeOfPart);

    List<Service> findByCostOfPartBetween(BigDecimal min, BigDecimal max);
}