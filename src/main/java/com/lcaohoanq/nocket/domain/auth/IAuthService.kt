package com.lcaohoanq.nocket.domain.auth

import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.domain.user.UserPort
import java.util.*

interface IAuthService {

    @Throws(Exception::class)
    open fun register(accountRegisterDTO: AuthPort.AccountRegisterDTO): User

    @Throws(Exception::class)
    open fun login(email: String, password: String): AuthPort.LoginResponse

    @Throws(Exception::class)
    open fun getUserDetailsFromToken(token: String): UserPort.UserResponse

    @Throws(Exception::class)
    open fun logout(token: String, user: User)

    @Throws(Exception::class)
    open fun verifyOtpToVerifyUser(userId: UUID, otp: String)

    @Throws(Exception::class)
    open fun verifyOtpIsCorrect(userId: UUID, otp: String)

}