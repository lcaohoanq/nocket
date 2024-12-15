package com.lcaohoanq.nocket.domain.friendship;

import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.enums.FriendShipStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // Find by requester or addressee
    @Query("SELECT f FROM Friendship f WHERE f.requester = :user OR f.addressee = :user")
    List<Friendship> findByUser(@Param("user") User user);

    // Find by requester or addressee and status
    @Query("SELECT f FROM Friendship f WHERE (f.requester = :user OR f.addressee = :user) AND f.status = :status")
    List<Friendship> findByUserAndStatus(@Param("user") User user, @Param("status") FriendShipStatus status);

    // Find by requester and addressee
    Friendship findByRequesterAndAddressee(User requester, User addressee);
}
