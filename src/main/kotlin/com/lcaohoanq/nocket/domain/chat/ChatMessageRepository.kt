package com.lcaohoanq.nocket.domain.chat

import org.springframework.data.jpa.repository.JpaRepository

interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    fun findByChatId(chatId: String): List<ChatMessage>
}
