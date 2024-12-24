package com.lcaohoanq.nocket.domain.auth

import com.lcaohoanq.nocket.annotation.RetryAndBlock
import com.lcaohoanq.nocket.api.ApiResponse
import com.lcaohoanq.nocket.domain.localization.LocalizationUtils
import com.lcaohoanq.nocket.api.ApiConstant
import com.lcaohoanq.nocket.domain.localization.MessageKey
import com.lcaohoanq.nocket.domain.auth.AuthPort.AccountRegisterDTO
import com.lcaohoanq.nocket.domain.auth.AuthPort.VerifyUserDTO
import com.lcaohoanq.nocket.domain.user.IUserService
import com.lcaohoanq.nocket.domain.user.UserPort.UserResponse
import com.lcaohoanq.nocket.exception.MethodArgumentNotValidException
import com.lcaohoanq.nocket.mapper.UserMapper
import io.micrometer.core.annotation.Timed
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*

@Slf4j
@RequestMapping("${ApiConstant.API_PREFIX}/auth")
@RestController
class AuthController(
    private val userService: IUserService,
    private val localizationUtils: LocalizationUtils,
    private val request: HttpServletRequest,
    private val authService: IAuthService,
    private val userMapper: UserMapper,
) {

    private val logger = KotlinLogging.logger {}

    @Timed(
        value = "custom.login.requests",
        extraTags = ["uri", "/api/v1/users/login"],
        description = "Track login request count"
    )
    @PostMapping("/login")
    fun login(
        @RequestBody @Valid userLoginDTO: AuthPort.UserLoginDTO,
        result: BindingResult
    ): ResponseEntity<ApiResponse<AuthPort.LoginResponse>> {
        if (result.hasErrors()) {
            throw MethodArgumentNotValidException(result)
        }

        return ResponseEntity.ok(
            ApiResponse.builder<AuthPort.LoginResponse>()
                .message(localizationUtils.getLocalizedMessage(MessageKey.LOGIN_SUCCESSFULLY))
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(
                    authService.login(
                        userLoginDTO.email,
                        userLoginDTO.password
                    )
                )
                .build()
        );

    }

    @Timed(
        value = "custom.register.requests",
        extraTags = ["uri", "/api/v1/users/register"],
        description = "Track register request count"
    )
    @PostMapping("/register")
    @Throws(
        Exception::class
    )
    fun createUser(
        @RequestBody accountRegisterDTO: @Valid AccountRegisterDTO?,
        result: BindingResult
    ): ResponseEntity<ApiResponse<UserResponse>> {
        if (result.hasErrors()) {
            throw MethodArgumentNotValidException(result)
        }

        val user = authService.register(accountRegisterDTO!!)
        logger.info("New user registered successfully")
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.builder<UserResponse>()
                .message(
                    localizationUtils.getLocalizedMessage(MessageKey.REGISTER_SUCCESSFULLY)
                )
                .statusCode(HttpStatus.CREATED.value())
                .isSuccess(true)
                .data(userMapper.toUserResponse(user))
                .build()
        )
    }

    @Timed(
        value = "custom.logout.requests",
        extraTags = ["uri", "/api/v1/users/logout"],
        description = "Track logout request count"
    )
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    @PostMapping("/logout")
    @Throws(
        Exception::class
    )
    fun logout(): ResponseEntity<ApiResponse<Objects>> {
        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7)

            val userDetails = SecurityContextHolder.getContext()
                .authentication.principal as UserDetails
            val user = userService.findByUsername(userDetails.username)

            authService.logout(token, user) //revoke token

            return ResponseEntity.ok().body(
                ApiResponse.builder<Objects>()
                    .message(
                        localizationUtils.getLocalizedMessage(
                            MessageKey.LOGOUT_SUCCESSFULLY
                        )
                    )
                    .statusCode(HttpStatus.OK.value())
                    .isSuccess(true)
                    .build()
            )
        } else {
            return ResponseEntity.badRequest().body(
                ApiResponse.builder<Objects>()
                    .message(
                        localizationUtils.getLocalizedMessage(
                            MessageKey.LOGOUT_FAILED
                        )
                    )
                    .reason("Authorization header is missing")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .isSuccess(false)
                    .build()
            )
        }
    }

    @PutMapping("/verify/{otp}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    @Throws(
        Exception::class
    )
    fun verifiedUser(
        @PathVariable otp: Int
    ): ResponseEntity<ApiResponse<OtpResponse>> {
        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetails
        val user = userService.findByUsername(userDetails.username)

        authService.verifyOtpToVerifyUser(user.id, otp.toString())
        return ResponseEntity.ok().body(
            ApiResponse.builder<OtpResponse>()
                .message(MessageKey.VERIFY_USER_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .build()
        )
    }

    @Timed(
        value = "custom.verify.requests",
        extraTags = ["uri", "/api/v1/users/verify"],
        description = "Track verify request count"
    )
    @RetryAndBlock(maxAttempts = 3, blockDurationSeconds = 3600, maxDailyAttempts = 6)
    @PostMapping("/send-verify-otp")
    @Throws(
        Exception::class
    )
    fun verifiedUserNotLogin(
        @RequestBody verifyUserDTO: @Valid VerifyUserDTO?,
        result: BindingResult
    ): ResponseEntity<ApiResponse<OtpResponse>> {
        if (result.hasErrors()) {
            throw MethodArgumentNotValidException(result)
        }

        val user = userService.findUserByEmail(verifyUserDTO!!.email)
        authService.verifyOtpToVerifyUser(user.id, verifyUserDTO.otp)
        return ResponseEntity.ok().body(
            ApiResponse.builder<OtpResponse>()
                .message(MessageKey.VERIFY_USER_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .build()
        )
    }


}