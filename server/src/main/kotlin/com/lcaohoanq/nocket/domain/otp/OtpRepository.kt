package com.lcaohoanq.nocket.domain.otp

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.*

interface OtpRepository : JpaRepository<Otp, UUID> {
    fun findByEmailAndOtp(email: String, otp: String): Optional<Otp>

    @Modifying
    @Query("UPDATE Otp o SET o.isExpired = true WHERE o.expiredAt < :now AND o.isExpired = false")
    fun updateExpiredOtps(now: LocalDateTime)
}
