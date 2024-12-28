package com.lcaohoanq.nocket.domain.token

import com.lcaohoanq.nocket.base.exception.DataNotFoundException
import com.lcaohoanq.nocket.domain.jwt.JwtTokenUtils
import com.lcaohoanq.nocket.domain.jwt.JWTConfig
import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.domain.user.UserService
import com.lcaohoanq.nocket.exception.ExpiredTokenException
import com.lcaohoanq.nocket.exception.TokenNotFoundException
import com.lcaohoanq.nocket.domain.user.UserMapper
import jakarta.transaction.Transactional
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
@Slf4j
class TokenService(
    private val userService: UserService,
    private val jwtConfig: JWTConfig,
    private val tokenRepository: TokenRepository,
    private val jwtTokenUtil: JwtTokenUtils,
    private val userMapper: UserMapper
) : ITokenService {
    
    @Transactional
    @Throws(Exception::class)
    override fun refreshToken(refreshToken: String, user: User): Token {
        val existingToken = tokenRepository.findByRefreshToken(refreshToken)
        if (existingToken.refreshExpirationDate!!.isBefore(LocalDateTime.now())) {
            tokenRepository.delete(existingToken)
            throw ExpiredTokenException("Refresh token is expired")
        }
        val token = jwtTokenUtil.generateToken(user)
        val expirationDateTime = LocalDateTime.now().plusSeconds(jwtConfig.expiration.toLong())
        existingToken.expirationDate = expirationDateTime
        existingToken.token = token
        existingToken.refreshToken = UUID.randomUUID().toString()
        existingToken.refreshExpirationDate =
            LocalDateTime.now().plusSeconds(jwtConfig.expirationRefreshToken.toLong())
        return existingToken
    }

    //do revoke token
    @Throws(DataNotFoundException::class)
    override fun deleteToken(token: String, user: User) {
        val existingToken = tokenRepository.findByToken(token)
        if (java.lang.Boolean.TRUE == existingToken.revoked) {
            throw TokenNotFoundException("Token has been revoked")
        }
        //check if token is attaching with user
        if (existingToken.user?.id != user.id) {
            throw TokenNotFoundException("Token does not exist")
        }
        existingToken.revoked = true
        tokenRepository.save(existingToken)
    }

    @Throws(DataNotFoundException::class)
    override fun findUserByToken(token: String): Token {
        return tokenRepository.findByToken(token)
    }

    @Transactional
    override fun addToken(userId: UUID, token: String, isMobileDevice: Boolean): Token {
        val existingUser = userService.findUserById(userId)
        val userTokens = tokenRepository.findByUserId(existingUser.id)
        val tokenCount = userTokens.size
        // Số lượng token vượt quá giới hạn, xóa một token cũ
        if (tokenCount >= MAX_TOKENS) {
            //kiểm tra xem trong danh sách userTokens có tồn tại ít nhất
            //một token không phải là thiết bị di động (non-mobile)
            val hasNonMobileToken = !userTokens.stream().allMatch(Token::isMobile)
            val tokenToDelete = if (hasNonMobileToken) {
                userTokens.stream()
                    .filter { userToken: Token -> !userToken.isMobile }
                    .findFirst()
                    .orElse(userTokens[0])
            } else {
                //tất cả các token đều là thiết bị di động,
                //chúng ta sẽ xóa token đầu tiên trong danh sách
                userTokens[0]
            }
            tokenRepository.delete(tokenToDelete)
        }
        val expirationInSeconds = jwtConfig.expiration.toLong()
        val expirationDateTime = LocalDateTime.now().plusSeconds(expirationInSeconds)

        // Tạo mới một token cho người dùng
        val newToken = Token()
        newToken.user = userMapper.toUser(existingUser)
        newToken.token = token
        newToken.revoked = false
        newToken.expired = false
        newToken.tokenType = "Bearer"
        newToken.expirationDate = expirationDateTime
        newToken.isMobile = isMobileDevice

        newToken.refreshToken = UUID.randomUUID().toString()
        newToken.refreshExpirationDate = LocalDateTime.now().plusSeconds(
            jwtConfig.expirationRefreshToken.toLong()
        )
        tokenRepository.save(newToken)
        return newToken
    }

    companion object {
        private const val MAX_TOKENS = 3
    }
}
