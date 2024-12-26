package com.lcaohoanq.nocket.domain.otp

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class OtpService(
    private val otpRepository: OtpRepository
) : IOtpService {
    
    override fun createOtp(newOtp: Otp): Otp {
        with(newOtp) {
            otp = newOtp.otp
            email = newOtp.email
            expiredAt = newOtp.expiredAt
            isUsed = newOtp.isUsed
            isExpired = newOtp.isExpired
        }
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
        val range = 100000..999999
        return range.random().toString()
    }
}
