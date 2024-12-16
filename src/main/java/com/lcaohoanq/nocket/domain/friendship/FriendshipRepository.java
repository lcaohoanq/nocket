package com.lcaohoanq.nocket.domain.friendship;

import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.enums.FriendShipStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    @Query("SELECT f FROM Friendship f WHERE " +
        "(f.user1 = :user OR f.user2 = :user) " +
        "AND f.status = :status")
    List<Friendship> findFriendshipsByUserAndStatus(
        @Param("user") User user,
        @Param("status") FriendShipStatus status
    );

    @Query("SELECT f FROM Friendship f WHERE " +
        "((f.user1 = :user1 AND f.user2 = :user2) OR " +
        "(f.user1 = :user2 AND f.user2 = :user1))")
    Optional<Friendship> findFriendshipBetweenUsers(
        @Param("user1") User user1,
        @Param("user2") User user2
    );
    
}
