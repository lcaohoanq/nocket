package com.lcaohoanq.nocket.domain.auth

import com.lcaohoanq.nocket.base.exception.DataNotFoundException
import com.lcaohoanq.nocket.domain.jwt.JwtTokenUtils
import com.lcaohoanq.nocket.domain.localization.LocalizationUtils
import com.lcaohoanq.nocket.domain.localization.MessageKey
import com.lcaohoanq.nocket.constant.Regex
import com.lcaohoanq.nocket.domain.avatar.Avatar
import com.lcaohoanq.nocket.domain.mail.IMailService
import com.lcaohoanq.nocket.domain.otp.Otp
import com.lcaohoanq.nocket.domain.otp.OtpService
import com.lcaohoanq.nocket.domain.socialaccount.SocialAccountRepository
import com.lcaohoanq.nocket.domain.token.TokenService
import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.domain.user.UserPort
import com.lcaohoanq.nocket.domain.user.UserRepository
import com.lcaohoanq.nocket.domain.user.UserService
import com.lcaohoanq.nocket.domain.wallet.Wallet
import com.lcaohoanq.nocket.domain.wallet.WalletRepository
import com.lcaohoanq.nocket.enums.Country
import com.lcaohoanq.nocket.enums.Currency
import com.lcaohoanq.nocket.enums.UserRole
import com.lcaohoanq.nocket.enums.UserStatus
import com.lcaohoanq.nocket.exception.ExpiredTokenException
import com.lcaohoanq.nocket.exception.MalformBehaviourException
import com.lcaohoanq.nocket.exception.PasswordWrongFormatException
import com.lcaohoanq.nocket.mapper.TokenMapper
import com.lcaohoanq.nocket.mapper.UserMapper
import com.lcaohoanq.nocket.metadata.MediaMeta
import com.lcaohoanq.nocket.util.Identifiable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import jakarta.servlet.http.HttpServletRequest
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.LocalDateTime
import java.util.*

@Slf4j
@Service
open class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtils: JwtTokenUtils,
    private val socialAccountRepository: SocialAccountRepository,
    private val localizationUtils: LocalizationUtils,
    private val mailService: IMailService,
    private val tokenService: TokenService,
    private val otpService: OtpService,
    private val userService: UserService,
    private val walletRepository: WalletRepository,
    private val userMapper: UserMapper,
    private val tokenMapper: TokenMapper,
    val request: HttpServletRequest,
) : IAuthService, Identifiable {

    private val logger = KotlinLogging.logger {}

    override fun register(accountRegisterDTO: AuthPort.AccountRegisterDTO): User {
        if (!accountRegisterDTO.password.matches(Regex.PASSWORD_REGEX.toRegex())) {
            throw PasswordWrongFormatException(
                "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
            )
        }

        if (accountRegisterDTO.password != accountRegisterDTO.confirmPassword) {
            throw MalformBehaviourException("Password and confirm password must be the same")
        }

        var email = accountRegisterDTO.email
        if (userRepository.existsByEmail(email)) {
            throw DataIntegrityViolationException(
                localizationUtils.getLocalizedMessage(MessageKey.EMAIL_ALREADY_EXISTS)
            )
        }

        if (userRepository.existsByPhoneNumber(accountRegisterDTO.phoneNumber)) {
            throw DataIntegrityViolationException("Phone number already exists")
        }

        val request: HttpServletRequest =
            (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)
                ?.request ?: throw NullPointerException("RequestAttributes are null")

        val acceptLanguage = request.getHeader("Accept-Language")

        var preferredLanguage = accountRegisterDTO.preferredLanguage
            ?: if (acceptLanguage.isNullOrEmpty()) Country.UNITED_STATES
            else Country.valueOf(acceptLanguage.uppercase())

        var preferredCurrency = accountRegisterDTO.preferredCurrency ?: Currency.USD

        return Single.fromCallable {
            // 1. First create the User without avatars
            var newUser = User().apply {
                name = accountRegisterDTO.name
                email = accountRegisterDTO.email
                hashedPassword = passwordEncoder.encode(accountRegisterDTO.password)
                phoneNumber = accountRegisterDTO.phoneNumber
                isActive = true
                gender = accountRegisterDTO.gender
                status = UserStatus.UNVERIFIED
                dateOfBirth = accountRegisterDTO.dateOfBirth
                role = UserRole.MEMBER
            }

            // 2. Save the user first
            newUser = userRepository.save(newUser)

            // 3. Create and set the avatar with the saved user
            val avatar = Avatar().apply {
                mediaMeta = MediaMeta().apply {
                    imageUrl =
                        "https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg"
                }
                user = newUser
            }

            // 4. Add avatar to user's avatar list
            newUser.avatars = listOf(avatar)

            // 5. Create and save the wallet
            val newWallet = Wallet().apply {
                // 5.1 Set the wallet balance to 0
                balance = 0f
                user = newUser
            }
                .let { walletRepository.save(it) }

            newUser.wallet = newWallet

            // 6. Save everything
            userRepository.save(newUser)
        }
            .flatMap { mailService.createEmailVerification(it) }
            .subscribeOn(Schedulers.io())
            .blockingGet()
    }

    override fun login(email: String, password: String): AuthPort.LoginResponse {

        val existingUser = userRepository.findByEmail(email)?.let {
            it.get() // get the value from Optional
        } ?: throw DataIntegrityViolationException(
            localizationUtils.getLocalizedMessage(MessageKey.WRONG_PHONE_PASSWORD)
        )

        // Update last login timestamp and save user
        existingUser.apply {
            lastLoginTimestamp = LocalDateTime.now()
            activityStatus = User.ActivityStatus.ONLINE
            userRepository.save(this)
        }

        val authenticationToken =
            UsernamePasswordAuthenticationToken(email, password, existingUser.authorities)
        authenticationManager.authenticate(authenticationToken)

        val token = jwtTokenUtils.generateToken(existingUser)
        val userResponse = userMapper.toUserResponse(existingUser)
        val jwtToken = tokenService.addToken(
            userResponse.id,
            token,
            isMobileDevice(request.getHeader("User-Agent"))
        )

        logger.info("User logged in successfully")

        return AuthPort.LoginResponse(tokenMapper.toTokenResponse(jwtToken), userResponse)
    }

    override fun getUserDetailsFromToken(token: String): UserPort.UserResponse {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw ExpiredTokenException("Token is expired");
        }
        val email = jwtTokenUtils.extractEmail(token)
        val user = userRepository.findByEmail(email)

        if (!user.isPresent) {
            throw Exception(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
            )
        }
        return userMapper.toUserResponse(user.get())
    }

    override fun logout(token: String, user: User) {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw ExpiredTokenException("Token is expired")
        }
        
        with(user) {
            activityStatus = User.ActivityStatus.OFFLINE
            userRepository.save(this)
        }

        tokenService.deleteToken(token, user)
    }

    @Transactional
    override fun verifyOtpToVerifyUser(userId: UUID, otp: String) {
        val user = userRepository.findById(userId)
            .orElseThrow { DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)) }

        if (user.status === UserStatus.VERIFIED) {
            throw MalformBehaviourException(localizationUtils.getLocalizedMessage(MessageKey.USER_ALREADY_VERIFIED))
        }

        if (user.status === UserStatus.BANNED) {
            throw MalformBehaviourException("User is banned")
        }

        val otpEntity = user.email?.let { getOtpByEmailOtp(it, otp) }

        if (otpEntity == null) throw DataIntegrityViolationException("OTP is not correct, please try again later")

        if (otpEntity.expiredAt?.isBefore(LocalDateTime.now()) == true) {
            otpEntity.isExpired = true
            otpService.disableOtp(otpEntity.id)
            throw DataIntegrityViolationException("OTP is expired, please try again later")
        }

        if (!otpEntity.otp.equals(otp)) throw DataIntegrityViolationException("OTP is not correct, please try again later")

        otpEntity.isUsed = true
        otpService.disableOtp(otpEntity.id)
        user.status = UserStatus.VERIFIED
        userRepository.save(user)
    }

    private fun getOtpByEmailOtp(email: String, otp: String): Otp =
        otpService.getOtpByEmailOtp(email, otp)
            .orElseThrow { DataIntegrityViolationException("OTP is not correct, please try again later") }

    @Transactional
    override fun verifyOtpIsCorrect(userId: UUID, otp: String) {

        val user = userService.findUserById(userId)

        val otpEntity = getOtpByEmailOtp(user.email, otp)

        if (otpEntity.expiredAt?.isBefore(LocalDateTime.now()) == true) {
            otpEntity.isExpired = true
            otpService.disableOtp(otpEntity.id)
            throw DataIntegrityViolationException(
                localizationUtils.getLocalizedMessage(MessageKey.OTP_EXPIRED)
            )
        }

        if (!otpEntity.otp.equals(otp)) {
            throw DataIntegrityViolationException("OTP is not correct, please try again later")
        }

        otpEntity.isUsed = true
        otpService.disableOtp(otpEntity.id)
    }


}