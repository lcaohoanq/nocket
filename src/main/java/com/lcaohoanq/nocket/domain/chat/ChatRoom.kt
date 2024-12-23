package com.lcaohoanq.nocket.domain.chat

import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.domain.user.User
import jakarta.persistence.*
import org.hibernate.envers.Audited

@Audited
@Entity
@Table(name = "chat_rooms")
class ChatRoom : BaseEntity() {
    @ManyToOne
    @JoinColumn(name = "user_1_id", nullable = false)
    private val user1: User? = null

    @ManyToOne
    @JoinColumn(name = "user_2_id", nullable = false)
    private val user2: User? = null

    @Column(name = "user1_typing_status")
    private var user1TypingStatus = false

    @Column(name = "user2_typing_status")
    private var user2TypingStatus = false
}