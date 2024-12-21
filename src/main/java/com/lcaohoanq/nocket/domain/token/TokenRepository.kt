package com.lcaohoanq.nocket.domain.token

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

interface TokenRepository : JpaRepository<Token, UUID?> {
    fun findByUserId(userId: UUID?): List<Token>

    fun findByToken(token: String?): Token

    fun findByRefreshToken(token: String?): Token?

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expirationDate < :now OR t.expired = true")
    fun deleteExpiredTokens(@Param("now") now: LocalDateTime?)
}
