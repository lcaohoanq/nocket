package com.lcaohoanq.nocket.domain.chat

import com.lcaohoanq.nocket.base.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.envers.Audited
import java.time.LocalDateTime

@Audited
@Entity
@Table(name = "chat_messages")
class ChatMessage : BaseEntity() {
    @ManyToOne
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id", nullable = false)
    private val chatRoomId: ChatRoom? = null

    private val senderId: Long? = null
    private val content: String? = null
    private val attachmentUrl: String? = null
    private val timestamp: LocalDateTime? = null
    private val isRead = false
}