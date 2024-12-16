package com.lcaohoanq.nocket.domain.user;

import com.lcaohoanq.nocket.enums.UserRole;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByRole(UserRole role);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    Page<User> findAllUserWithRole(Pageable pageable,
                                   @Param("role") UserRole role);

    @Modifying
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    void softDeleteUser(UUID id);

    @Modifying
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :id")
    void restoreUser(UUID id);
}
