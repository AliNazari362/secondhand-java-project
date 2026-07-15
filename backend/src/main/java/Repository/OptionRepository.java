package Repository;

import entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    @Query("SELECT o FROM Option o WHERE o.adv.id = :advId")
    List<Option> findByAdvId(@Param("advId") UUID advId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Option o WHERE o.adv.id = :advId")
    void deleteByAdvId(@Param("advId") UUID advId);
}