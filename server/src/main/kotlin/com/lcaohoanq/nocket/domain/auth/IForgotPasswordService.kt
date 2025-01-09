package com.lcaohoanq.nocket.domain.auth

import com.lcaohoanq.nocket.domain.user.User
import jakarta.mail.MessagingException

interface IForgotPasswordService {
    @Throws(MessagingException::class)
    fun sendEmailOtp(existingUser: User)
}
