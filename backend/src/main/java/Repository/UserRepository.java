package Repository;

import Entity.User;
import Entity.enums.UserStatus;
import Entity.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByUserStatus(UserStatus status);

    List<User> findByUserType(UserType userType);

    List<User> findByUserStatusAndUserType(UserStatus status, UserType userType);
}