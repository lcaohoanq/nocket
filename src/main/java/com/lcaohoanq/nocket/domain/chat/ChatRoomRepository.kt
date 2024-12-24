package com.lcaohoanq.nocket.domain.chat

import com.lcaohoanq.nocket.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ChatRoomRepository : JpaRepository<ChatRoom, UUID> {
    fun findByUser1AndUser2(user1: User, user2: User): Optional<ChatRoom>
}
