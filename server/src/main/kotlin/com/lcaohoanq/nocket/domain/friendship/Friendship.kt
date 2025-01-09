package com.lcaohoanq.nocket.domain.friendship

import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.enums.FriendShipStatus
import jakarta.persistence.*
import org.hibernate.envers.Audited

@Audited
@Entity
@Table(
    name = "friendships",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user1_id", "user2_id"])]
)
class Friendship : BaseEntity() {
    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    var user1: User? = null

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    var user2: User? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: FriendShipStatus? = null

    // Ensure user1 and user2 are always in a consistent order
    fun normalizeUsers() {
        if (user1!!.id.compareTo(user2!!.id) > 0) {
            val temp = user1
            user1 = user2
            user2 = temp
        }
    }
}
