package com.lcaohoanq.nocket.domain.role;

import com.lcaohoanq.nocket.enums.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByUserRole(UserRole userRole);

}
