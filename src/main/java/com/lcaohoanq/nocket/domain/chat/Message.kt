package com.lcaohoanq.nocket.domain.chat

import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.domain.user.User
import jakarta.persistence.*
import org.hibernate.envers.Audited

@Audited
@Entity
@Table(name = "messages")
class Message : BaseEntity() {
    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private val chatRoom: ChatRoom? = null

    @OneToOne(cascade = [CascadeType.ALL]) // If a message has one attachment
    @JoinColumn(name = "attachment_id")
    private val attachment: Attachment? = null

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private val user: User? = null

    @Column(name = "content", nullable = false)
    private var content: String? = null // Store the message text

    @Column(name = "is_read", nullable = false)
    private var isRead = false // Timestamps from BaseEntity are inherited
}

