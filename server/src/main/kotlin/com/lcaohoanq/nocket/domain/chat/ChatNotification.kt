package com.lcaohoanq.nocket.domain.chat

data class ChatNotification(
    val id: Long,
    val senderId: String,
    val recipientId: String,
    val content: String
) 