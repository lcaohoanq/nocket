package com.lcaohoanq.nocket.domain.mail

import com.lcaohoanq.nocket.domain.otp.Otp
import com.lcaohoanq.nocket.domain.otp.OtpService
import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.enums.EmailCategoriesEnum
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import jakarta.mail.MessagingException
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.time.LocalDateTime

@Slf4j
@Service
class MailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    private val otpService: OtpService
) : IMailService {

    private val logger = KotlinLogging.logger {}
    
    @Async
    @Throws(MessagingException::class)
    override fun sendMail(to: String, subject: String, templateName: String, context: Context) {
        val mimeMessage = mailSender.createMimeMessage()
        try {
            val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")
            val htmlContent = templateEngine.process(templateName, context)
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(htmlContent, true)
            mailSender.send(mimeMessage)
            logger.info("Mail send successfully to {}", to)
        } catch (e: MessagingException) {
            logger.error("Failed to send mail to {}: {}", to, e.message)
            throw MessagingException("Failed to send mail to $to")
        }
    }

    override fun createEmailVerification(user: User): Single<User> {
        return Single.fromCallable {
            val otp = otpService.generateOtp()
            // Create email context
            val context = Context()
            context.setVariable("name", user.name)
            context.setVariable("otp", otp)

            // Send email
            this.sendMail(
                user.email!!,
                "Verify your email",
                EmailCategoriesEnum.OTP.type,
                context
            )

            // Create OTP record
            val otpEntity = Otp()
            otpEntity.otp = otp
            otpEntity.email = user.email
            otpEntity.expiredAt = LocalDateTime.now().plusMinutes(5)
            otpEntity.isUsed = false
            otpEntity.isExpired = false

            otpService.createOtp(otpEntity)
            user // Return the user for chaining
        }.subscribeOn(Schedulers.io())
    }
}