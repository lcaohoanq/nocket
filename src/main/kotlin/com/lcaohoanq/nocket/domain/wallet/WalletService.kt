package com.lcaohoanq.nocket.domain.wallet

import com.lcaohoanq.nocket.base.exception.DataNotFoundException
import com.lcaohoanq.nocket.domain.localization.LocalizationUtils
import com.lcaohoanq.nocket.domain.localization.MessageKey
import com.lcaohoanq.nocket.domain.mail.IMailService
import com.lcaohoanq.nocket.domain.user.IUserService
import com.lcaohoanq.nocket.domain.user.UserRepository
import com.lcaohoanq.nocket.domain.wallet.WalletPort.WalletResponse
import com.lcaohoanq.nocket.enums.EmailCategoriesEnum
import com.lcaohoanq.nocket.mapper.WalletMapper
import jakarta.mail.MessagingException
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.context.Context
import java.util.*

@Service
@Slf4j
open class WalletService(
    private val walletRepository: WalletRepository,
    private val userService: IUserService,
    private val userRepository: UserRepository,
    private val localizationUtils: LocalizationUtils,
    private val mailService: IMailService,
    private val walletMapper: WalletMapper,
) : IWalletService {

    private val log = KotlinLogging.logger {}

    override fun getByUserId(userId: UUID): WalletResponse {
        val existedUser = userService!!.findUserById(userId)
        return walletMapper!!.toWalletResponse(walletRepository!!.findByUserId(existedUser.id))
    }

    @Transactional
    @Retryable(
        retryFor = [MessagingException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 2000)
    )
    @Throws(
        Exception::class
    )
    override fun updateAccountBalance(userId: UUID, payment: Long) {
        val existingUser = userRepository!!.findById(userId)
            .orElseThrow {
                DataNotFoundException(
                    localizationUtils!!.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
                )
            }
        
        with(existingUser.wallet) {
            this!!.balance = this.balance!! + payment
        }

        val context = Context()
        context.setVariable("name", existingUser.name)
        context.setVariable("amount", payment)
        context.setVariable("balance", existingUser.wallet!!.balance)

        try {
            mailService!!.sendMail(
                existingUser.email,
                "Account balance updated",
                EmailCategoriesEnum.BALANCE_FLUCTUATION.type,
                context
            )
        } catch (e: MessagingException) {
            log.error("Failed to send email to {}", existingUser.email, e)
            throw MessagingException(
                String.format(
                    "Failed to send email to %s",
                    existingUser.email
                )
            )
        }

        log.info(
            "User {} balance updated. New balance: {}", userId,
            existingUser.wallet!!.balance
        )
        userRepository.save(existingUser)
    }
}
