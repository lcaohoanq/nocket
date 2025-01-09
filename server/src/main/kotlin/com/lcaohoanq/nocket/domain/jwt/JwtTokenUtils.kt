package com.lcaohoanq.nocket.domain.jwt

import com.lcaohoanq.nocket.domain.token.TokenRepository
import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.exception.InvalidParamException
import com.lcaohoanq.nocket.exception.JwtAuthenticationException
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import java.security.Key
import java.security.SecureRandom
import java.util.*
import java.util.function.Function

@Component
@Slf4j
class JwtTokenUtils(
    private val tokenRepository: TokenRepository,
    private val jwtConfig: JWTConfig
) {

    //    private final TokenRepository tokenRepository;
    @Throws(Exception::class)
    fun generateToken(user: User): String {
        //properties => claims
        val claims: MutableMap<String, Any?> = HashMap()
        //this.generateSecretKey();
        claims["email"] = user.email
        claims["userId"] = user.id
        try {
            //how to extract claims from this ?
            return Jwts.builder()
                .setClaims(claims) //how to extract claims from this ?
                .setSubject(user.email)
                .setExpiration(Date(System.currentTimeMillis() + jwtConfig.expiration * 1000L))
                .signWith(signInKey, SignatureAlgorithm.HS256)
                .compact()
        } catch (e: Exception) {
            //you can "inject" Logger, instead System.out.println
            throw InvalidParamException("Cannot create jwt token, error: " + e.message)
            //return null;
        }
    }

    private val signInKey: Key
        get() {
            val bytes = Decoders.BASE64.decode(jwtConfig.secretKey)
            //Keys.hmacShaKeyFor(Decoders.BASE64.decode("TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI="));
            return Keys.hmacShaKeyFor(bytes)
        }

    private fun generateSecretKey(): String {
        val random = SecureRandom()
        val keyBytes = ByteArray(32) // 256-bit key
        random.nextBytes(keyBytes)
        return Encoders.BASE64.encode(keyBytes)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(signInKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = this.extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    //check expiration
    fun isTokenExpired(token: String): Boolean {
        val expirationDate = this.extractClaim(
            token
        ) { obj: Claims -> obj.expiration }
        return expirationDate.before(Date())
    }

    fun extractEmail(token: String): String {
        return extractClaim(
            token
        ) { obj: Claims -> obj.subject }
    }

    fun validateToken(token: String, userDetails: User): Boolean {
        try {
            val email = extractEmail(token)
            val existingToken = tokenRepository.findByToken(token)


            // Check token existence and revocation
            if (java.lang.Boolean.TRUE == existingToken.revoked) {
                throw JwtAuthenticationException("Token is invalid or has been revoked")
            }

            // Check token matches user
            if (email != userDetails.username) {
                throw JwtAuthenticationException("Token does not match user")
            }

            // Check expiration
            if (isTokenExpired(token)) {
                throw ExpiredJwtException(null, null, "Token has expired")
            }

            return true
        } catch (e: ExpiredJwtException) {
            throw JwtAuthenticationException("JWT token has expired")
        } catch (e: MalformedJwtException) {
            throw JwtAuthenticationException("Invalid JWT token format")
        } catch (e: UnsupportedJwtException) {
            throw JwtAuthenticationException("Unsupported JWT token")
        } catch (e: IllegalArgumentException) {
            throw JwtAuthenticationException("JWT claims string is empty")
        }
    }
}
