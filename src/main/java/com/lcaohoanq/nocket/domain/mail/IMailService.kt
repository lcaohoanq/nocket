package com.lcaohoanq.nocket.domain.mail

import com.lcaohoanq.nocket.domain.user.User
import io.reactivex.rxjava3.core.Single
import jakarta.mail.MessagingException
import org.thymeleaf.context.Context

interface IMailService {
    @Throws(MessagingException::class)
    fun sendMail(to: String, subject: String, templateName: String, context: Context)
    fun createEmailVerification(user: User): Single<User>
}
