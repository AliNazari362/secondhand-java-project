package Repository;

import entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByAdvId(UUID advId);

    void deleteByAdvId(UUID advId);
}