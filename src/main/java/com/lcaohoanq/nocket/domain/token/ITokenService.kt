package com.lcaohoanq.nocket.domain.token

import com.lcaohoanq.nocket.base.exception.DataNotFoundException
import com.lcaohoanq.nocket.domain.user.User
import org.springframework.stereotype.Service
import java.util.*

@Service
interface ITokenService {
    fun addToken(userId: UUID, token: String, isMobileDevice: Boolean): Token

    @Throws(Exception::class)
    fun refreshToken(refreshToken: String, user: User): Token

    @Throws(DataNotFoundException::class)
    fun deleteToken(token: String, user: User)

    @Throws(DataNotFoundException::class)
    fun findUserByToken(token: String): Token
}
