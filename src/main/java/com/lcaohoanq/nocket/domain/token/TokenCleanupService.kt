package com.lcaohoanq.nocket.domain.token

import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Slf4j
@RequiredArgsConstructor
class TokenCleanupService(
    private val tokenRepository: TokenRepository
) : ITokenCleanupService {

    private val logger = KotlinLogging.logger {}

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    @Async
    override fun cleanupExpiredTokens() {
        try {
            tokenRepository!!.deleteExpiredTokens(LocalDateTime.now())
        } catch (e: Exception) {
            // Log the exception and handle error
            logger.error("Error auto setting Token expired", e.cause)
        }
    }
}
