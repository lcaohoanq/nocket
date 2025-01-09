package com.lcaohoanq.nocket.domain.auth

import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.domain.user.UserPort
import java.util.*

interface IAuthService {

    @Throws(Exception::class)
    fun register(accountRegisterDTO: AuthPort.AccountRegisterDTO): User

    @Throws(Exception::class)
    fun login(email: String, password: String): AuthPort.LoginResponse

    @Throws(Exception::class)
    fun getUserDetailsFromToken(token: String): UserPort.UserResponse

    @Throws(Exception::class)
    fun logout(token: String, user: User)

    @Throws(Exception::class)
    fun verifyOtpToVerifyUser(userId: UUID, otp: String)

    @Throws(Exception::class)
    fun verifyOtpIsCorrect(userId: UUID, otp: String)

}