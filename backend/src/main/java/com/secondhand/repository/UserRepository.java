package com.secondhand.repository;

import com.secondhand.entity.User;
import com.secondhand.entity.enums.UserStatus;
import com.secondhand.entity.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link User} entities.
 *
 * <p>Provides standard CRUD operations inherited from {@link JpaRepository} as well as
 * custom lookup methods for finding users by email, status, and role.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Retrieves a user by their unique email address.
     *
     * @param email the email address to look up
     * @return an {@link Optional} containing the user if found, or empty if not
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given email address already exists.
     *
     * @param email the email address to check
     * @return {@code true} if a user with that email exists, {@code false} otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Returns all users with the specified account status.
     *
     * @param status the {@link UserStatus} to filter by (e.g., ACTIVE, BANNED)
     * @return list of matching users; empty list if none found
     */
    List<User> findByUserStatus(UserStatus status);

    /**
     * Returns all users with the specified role.
     *
     * @param userType the {@link UserType} to filter by (USER or ADMIN)
     * @return list of matching users; empty list if none found
     */
    List<User> findByUserType(UserType userType);

    /**
     * Returns all users matching both the given account status and role.
     *
     * @param status   the {@link UserStatus} to filter by
     * @param userType the {@link UserType} to filter by
     * @return list of users matching both criteria; empty list if none found
     */
    List<User> findByUserStatusAndUserType(UserStatus status, UserType userType);
}
