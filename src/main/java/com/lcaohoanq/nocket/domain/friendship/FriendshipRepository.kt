package com.lcaohoanq.nocket.domain.friendship

import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.enums.FriendShipStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface FriendshipRepository : JpaRepository<Friendship, UUID> {
    @Query(
        ("SELECT f FROM Friendship f WHERE " +
                "(f.user1 = :user OR f.user2 = :user) " +
                "AND f.status = :status")
    )
    fun findFriendshipsByUserAndStatus(
        @Param("user") user: User,
        @Param("status") status: FriendShipStatus
    ): List<Friendship>

    @Query(
        ("SELECT f FROM Friendship f WHERE " +
                "((f.user1 = :user1 AND f.user2 = :user2) OR " +
                "(f.user1 = :user2 AND f.user2 = :user1))")
    )
    fun findFriendshipBetweenUsers(
        @Param("user1") user1: User,
        @Param("user2") user2: User
    ): Optional<Friendship>
}
