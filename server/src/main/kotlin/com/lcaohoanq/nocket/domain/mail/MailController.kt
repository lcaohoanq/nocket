package com.lcaohoanq.nocket.domain.mail

import com.lcaohoanq.nocket.api.ApiConstant
import com.lcaohoanq.nocket.constant.EmailSubject
import com.lcaohoanq.nocket.domain.otp.IOtpService
import com.lcaohoanq.nocket.domain.otp.Otp
import com.lcaohoanq.nocket.domain.user.IUserService
import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.enums.EmailBlockReasonEnum
import com.lcaohoanq.nocket.enums.EmailCategoriesEnum
import jakarta.mail.MessagingException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.thymeleaf.context.Context
import java.time.LocalDateTime

@RequestMapping("${ApiConstant.API_PREFIX}/mail")
@RestController
class MailController(
    private val mailService: IMailService,
    private val request: HttpServletRequest,
    private val otpService: IOtpService,
    private val userService: IUserService
) {

    //api: /otp/send?type=email&recipient=abc@gmail
    @Throws(MessagingException::class)
    fun sendOtp(@RequestParam toEmail: String): ResponseEntity<MailPort.MailResponse> {
        val user = request.getAttribute("validatedEmail") as User

        val name = user.name
        val context = Context()
        val otp = otpService.generateOtp()
        context.setVariable("name", name)
        context.setVariable("otp", otp)
        mailService.sendMail(
            toEmail, EmailSubject.subjectGreeting(name),
            EmailCategoriesEnum.OTP.type,
            context
        )
        val response = MailPort.MailResponse("Mail sent successfully")

        val otpEntity = Otp()
        otpEntity.otp = otp
        otpEntity.email = toEmail
        otpEntity.expiredAt = LocalDateTime.now().plusMinutes(5)
        otpEntity.isUsed = false
        otpEntity.isExpired = false

        otpService.createOtp(otpEntity)

        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/block")
    @Throws(MessagingException::class)
    fun sendBlockAccount(@RequestParam toEmail: String): ResponseEntity<MailPort.MailResponse> {
        val user = request.getAttribute("validatedEmail") as User
        val context = Context()
        context.setVariable("reason", EmailBlockReasonEnum.ABUSE.reason)
        mailService.sendMail(
            toEmail, EmailSubject.subjectBlockEmail(user.name),
            EmailCategoriesEnum.BLOCK_ACCOUNT.type, context
        )
        val response = MailPort.MailResponse("Mail sent successfully")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping(path = ["/forgotPassword"])
    @Throws(MessagingException::class)
    fun sendForgotPassword(@RequestParam toEmail: String): ResponseEntity<MailPort.MailResponse> {
        val user = request.getAttribute("validatedEmail") as User
        val name = user.name
        val context = Context()
        val otp = otpService.generateOtp()
        context.setVariable("name", name)
        context.setVariable("otp", otp)
        mailService.sendMail(
            toEmail, EmailSubject.subjectGreeting(name),
            EmailCategoriesEnum.FORGOT_PASSWORD.type, context
        )
        val response = MailPort.MailResponse("Mail sent successfully")
        return ResponseEntity(response, HttpStatus.OK)
    }
}