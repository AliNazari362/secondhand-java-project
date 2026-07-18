package Repository;

import entity.ServiceObj;
import entity.ServiceObj.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceObj, UUID> {

    List<ServiceObj> findBySpecialCategory(String specialCategory);

    List<ServiceObj> findByTypeOfPart(ServiceType typeOfPart);

    List<ServiceObj> findByCostOfPartBetween(BigDecimal min, BigDecimal max);
}