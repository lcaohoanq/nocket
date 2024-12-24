package com.lcaohoanq.nocket.domain.otp

import java.util.*

interface IOtpService {
    fun createOtp(otp: Otp): Otp
    fun disableOtp(id: UUID)
    fun getOtpByEmailOtp(email: String, otp: String): Optional<Otp>
    fun setOtpExpired()
    fun generateOtp(): String
}
