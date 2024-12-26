package com.lcaohoanq.nocket.domain.chat

import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {
    fun findBySenderIdAndRecipientId(senderId: String, recipientId: String): ChatRoom?
}
