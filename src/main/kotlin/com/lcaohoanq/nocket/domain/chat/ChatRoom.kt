package com.lcaohoanq.nocket.domain.chat

import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.domain.user.User
import jakarta.persistence.*
import org.hibernate.envers.Audited

@Audited
@Entity
@Table(name = "chat_rooms")
class ChatRoom(
    @Column(name = "chat_id")
    var chatId: String,

    @Column(name = "sender_id")
    var senderId: String,

    @Column(name = "recipient_id")
    var recipientId: String
) {
    constructor() : this("", "", "")

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
}