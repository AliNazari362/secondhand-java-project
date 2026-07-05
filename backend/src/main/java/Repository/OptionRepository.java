package Repository;

import entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByAdvId(UUID advId);

    void deleteByAdvId(UUID advId);
}