package com.lcaohoanq.nocket.domain.chat

interface IChatMessageService {
    fun save(chatMessage: ChatMessage): ChatMessage
    fun findChatMessages(senderId: String, recipientId: String): List<ChatMessage>
}
