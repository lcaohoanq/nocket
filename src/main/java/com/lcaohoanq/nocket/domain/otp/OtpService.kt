package com.lcaohoanq.nocket.domain.otp

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class OtpService(
    private val otpRepository: OtpRepository
) : IOtpService {
    
    override fun createOtp(otp: Otp): Otp {
        val newOtp = Otp()
        newOtp.otp = otp.otp
        newOtp.email = otp.email
        newOtp.expiredAt = otp.expiredAt
        newOtp.isUsed = otp.isUsed
        newOtp.isExpired = otp.isExpired
        return otpRepository.save(newOtp)
    }

    override fun disableOtp(id: UUID) {
        val existingOtp = otpRepository.findById(id).orElse(null) ?: return
        existingOtp.isExpired = true
        otpRepository.save(existingOtp)
    }

    override fun getOtpByEmailOtp(email: String, otp: String): Optional<Otp> {
        return otpRepository.findByEmailAndOtp(email, otp)
    }

    @Transactional
    override fun setOtpExpired() {
        val now = LocalDateTime.now()
        // Update OTPs where expired_at < now and is_expired = 0
        otpRepository.updateExpiredOtps(now)
    }

    override fun generateOtp(): String {
        return ((Math.random() * 9000).toInt() + 1000).toString()
    }
}
