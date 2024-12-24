package com.lcaohoanq.nocket.domain.auth

import com.lcaohoanq.nocket.api.ApiResponse
import com.lcaohoanq.nocket.domain.auth.AuthPort.ForgotPasswordResponse
import com.lcaohoanq.nocket.domain.auth.AuthPort.UpdatePasswordDTO
import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.domain.user.UserService
import com.lcaohoanq.nocket.exception.MethodArgumentNotValidException
import jakarta.mail.MessagingException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Slf4j
@RequestMapping("\${api.prefix}/forgot-password")
@RestController
class ForgotPasswordController(
    private val request: HttpServletRequest,
    private val forgotPasswordService: IForgotPasswordService,
    private val userService: UserService
) {

    @GetMapping("")
    @PreAuthorize("permitAll()")
    @Throws(MessagingException::class)
    fun forgotPassword(
        @Validated @RequestParam toEmail: String?
    ): ResponseEntity<ApiResponse<ForgotPasswordResponse>> {
        val user = request.getAttribute("validatedEmail") as User //get from aop

        forgotPasswordService.sendEmailOtp(user)

        val response =
            ForgotPasswordResponse(
                "Forgot password email sent successfully to " + user.email
            )

        return ResponseEntity.ok(
            ApiResponse.builder<ForgotPasswordResponse>()
                .message(response.message)
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .build()
        )
    }

    @PutMapping("")
    @PreAuthorize("permitAll()")
    @Throws(Exception::class)
    fun updatePassword(
        @RequestBody updatePasswordDTO: @Valid UpdatePasswordDTO,
        result: BindingResult
    ): ResponseEntity<ApiResponse<*>> {
        if (result.hasErrors()) {
            throw MethodArgumentNotValidException(result)
        }

        userService.updatePassword(updatePasswordDTO)
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.builder<Any>()
                .message("Password updated successfully")
                .isSuccess(true)
                .statusCode(HttpStatus.OK.value())
                .build()
        )
    }
}
