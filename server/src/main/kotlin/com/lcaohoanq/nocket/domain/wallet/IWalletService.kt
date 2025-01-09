package com.lcaohoanq.nocket.domain.wallet

import com.lcaohoanq.nocket.domain.wallet.WalletPort.WalletResponse
import jakarta.mail.MessagingException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import java.util.*

interface IWalletService {
    fun getByUserId(userId: UUID): WalletResponse
     
    @Retryable(
        retryFor = [MessagingException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 2000)
    )
    @Throws(Exception::class)
    fun updateAccountBalance(userId: UUID, payment: Long)
}
