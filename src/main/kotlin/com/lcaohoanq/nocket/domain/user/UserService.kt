package com.lcaohoanq.nocket.domain.user

import com.lcaohoanq.nocket.api.PageResponse
import com.lcaohoanq.nocket.base.exception.DataNotFoundException
import com.lcaohoanq.nocket.domain.jwt.JwtTokenUtils
import com.lcaohoanq.nocket.domain.localization.LocalizationUtils
import com.lcaohoanq.nocket.domain.localization.MessageKey
import com.lcaohoanq.nocket.domain.auth.AuthPort.UpdatePasswordDTO
import com.lcaohoanq.nocket.domain.mail.IMailService
import com.lcaohoanq.nocket.domain.otp.OtpService
import com.lcaohoanq.nocket.domain.socialaccount.SocialAccount
import com.lcaohoanq.nocket.domain.socialaccount.SocialAccountRepository
import com.lcaohoanq.nocket.domain.user.UserPort.UpdateUserDTO
import com.lcaohoanq.nocket.domain.user.UserPort.UserResponse
import com.lcaohoanq.nocket.enums.EmailCategoriesEnum
import com.lcaohoanq.nocket.enums.UserRole
import com.lcaohoanq.nocket.enums.UserStatus
import com.lcaohoanq.nocket.exception.*
import com.lcaohoanq.nocket.mapper.UserMapper
import com.lcaohoanq.nocket.util.PaginationConverter
import lombok.extern.slf4j.Slf4j
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.context.Context
import java.util.*

@Slf4j
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenUtils: JwtTokenUtils,
    private val socialAccountRepository: SocialAccountRepository,
    private val localizationUtils: LocalizationUtils,
    private val mailService: IMailService,
    private val otpService: OtpService,
    private val userMapper: UserMapper,
) : IUserService, PaginationConverter {

    override fun fetchUser(pageable: Pageable): PageResponse<UserResponse> {
        val usersPage = userRepository.findAll(pageable)
        return mapPageResponse(
            usersPage,
            pageable,
            { user: User -> userMapper.toUserResponse(user) },
            "Get all users successfully"
        )
    }

    @Throws(Exception::class)
    override fun loginOrRegisterGoogle(
        email: String, name: String, googleId: String,
        avatarUrl: String
    ): String {
        val optionalUser = userRepository.findByEmail(email)
        val user: User? = null
        var socialAccount: SocialAccount

        if (optionalUser.isEmpty) {
            // Register new user

            val newUser = User()
            newUser.email = email
            newUser.name = name
            newUser.role = UserRole.MEMBER
            newUser.status = UserStatus.VERIFIED
            newUser.isActive = true

            //            SocialAccount newSocialAccount = SocialAccount.builder()
//                .providerName(ProviderName.GOOGLE)
//                .name(name)
//                .email(email)
//                .build();
//
//            user = userRepository.save(newUser);
//            socialAccountRepository.save(newSocialAccount);
        }

        return jwtTokenUtils.generateToken(user!!)
    }

    @Throws(DataNotFoundException::class)
    override fun findUserById(id: UUID): UserResponse {
        return userRepository.findById(id)
            .map { user: User ->
                userMapper!!.toUserResponse(
                    user
                )
            }
            .orElseThrow {
                DataNotFoundException(
                    localizationUtils!!.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
                )
            }
    }

    @Throws(DataNotFoundException::class)
    override fun findUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow {
                DataNotFoundException(
                    "User not found: $email"
                )
            }
    }

    override fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    @Throws(DataNotFoundException::class)
    override fun findByUsername(username: String): User {
        return userRepository.findByEmail(username)
            .orElseThrow {
                DataNotFoundException(
                    localizationUtils!!.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
                )
            }
    }

    @Transactional
    @Throws(Exception::class)
    override fun updateUser(userId: UUID, updatedUserDTO: UpdateUserDTO): User {
        // Find the existing user by userId
        val existingUser = userRepository.findById(userId)
            .orElseThrow {
                DataNotFoundException(
                    localizationUtils!!.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
                )
            }

        // Check if the email is being changed and if it already exists for another user
        val newEmail = updatedUserDTO.email

        if (newEmail.isNotEmpty()) {
            // Check if the new email is different from the current user's email
            if (newEmail != existingUser.email) {
                // Check if the new email is already in use by another user
                val userWithNewEmail = userRepository.findByEmail(newEmail)

                if (userWithNewEmail.isPresent) {
                    throw EmailAlreadyUsedException("This email address is already registered")
                }

                // If not, update the current user's email
                existingUser.email = newEmail
            }
            // If the email is the same as the current one, no changes are needed
        } else {
            throw UpdateEmailException("This email cannot be empty")
        }

        // Check if the phoneNumber number is being changed and if it already exists for another user
        val newPhoneNumber = updatedUserDTO.phoneNumber

        if (newPhoneNumber.isNotEmpty()) {
            // Check if the new phoneNumber number is different from the current user's phoneNumber number
            if (newPhoneNumber != existingUser.phoneNumber) {
                // Check if the new phoneNumber number is already in use by another user
                val userWithNewPhoneNumber = userRepository.findByPhoneNumber(
                    newPhoneNumber
                )

                if (userWithNewPhoneNumber.isPresent) {
                    throw PhoneAlreadyUsedException(
                        "This phoneNumber number is already registered"
                    )
                }

                // If not, update the current user's phoneNumber number
                existingUser.phoneNumber = newPhoneNumber
            }
            // If the phoneNumber number is the same as the current one, no changes are needed
        } else {
            existingUser.phoneNumber = null
        }

        // Update user information based on the DTO
        existingUser.name = updatedUserDTO.name
        existingUser.status = UserStatus.valueOf(updatedUserDTO.status)
        existingUser.dateOfBirth = updatedUserDTO.dob

        //        if (updatedUserDTO.avatar() != null) {
//            existingUser.setAvatars(updatedUserDTO.avatar());
//        }

        // Update the password if it is provided in the DTO
        if (updatedUserDTO.password.isNotEmpty()) {
            if (updatedUserDTO.password != updatedUserDTO.confirmPassword) {
                throw DataNotFoundException("Password and confirm password must be the same")
            }
            val newPassword = updatedUserDTO.password
            val encodedPassword = passwordEncoder!!.encode(newPassword)
            existingUser.hashedPassword = encodedPassword
        }
        //existingUser.setRole(updatedRole);
        // Save the updated user
        return userRepository.save(existingUser)
    }

    @Throws(DataNotFoundException::class)
    override fun bannedUser(userId: UUID) {
        val user = userRepository.findById(userId)
            .orElseThrow {
                DataNotFoundException(
                    localizationUtils!!.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
                )
            }

        user.status = UserStatus.BANNED
        userRepository.save(user)
    }

    @Transactional
    @Throws(Exception::class)
    override fun updatePassword(updatePasswordDTO: UpdatePasswordDTO) {
        val existingUser = userRepository.findByEmail(updatePasswordDTO.email)
            .orElseThrow {
                DataNotFoundException(
                    localizationUtils!!.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
                )
            }

        if (!existingUser.isActive) {
            throw MalformBehaviourException(MessageKey.USER_NOT_FOUND)
        }

        if (existingUser.status != UserStatus.VERIFIED) {
            throw MalformBehaviourException("User do not verified their account")
        }

        if (existingUser.role == UserRole.MANAGER) {
            throw PermissionDeniedException("Cannot change password for this account")
        }

        existingUser.hashedPassword = passwordEncoder!!.encode(updatePasswordDTO.newPassword)

        mailService.sendMail(
            existingUser.email,
            "Password updated successfully",
            EmailCategoriesEnum.RESET_PASSWORD.type,
            Context()
        )

        userRepository.save(existingUser)
    }

    //need send email
    @Transactional
    @Throws(DataNotFoundException::class)
    override fun softDeleteUser(userId: UUID) {
        val existingUser = findUserById(userId)
        if (!existingUser.isActive) {
            throw MalformDataException("User is already deleted")
        }
        userRepository.softDeleteUser(userId)
    }

    @Transactional
    @Throws(DataNotFoundException::class)
    override fun restoreUser(userId: UUID) {
        val existingUser = findUserById(userId)
        if (existingUser.isActive) {
            throw MalformDataException("User is already active")
        }
        userRepository.restoreUser(userId)
    }

    @Throws(Exception::class)
    override fun findAll(keyword: String, pageable: Pageable): Page<User>? {
        return null
    }

    @Transactional
    @Throws(DataNotFoundException::class)
    override fun blockOrEnable(userId: UUID, active: Boolean) {
        val existingUser = userRepository.findById(userId)
            .orElseThrow {
                DataNotFoundException(
                    localizationUtils!!.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
                )
            }
        existingUser.isActive = active
        userRepository.save(existingUser)
    }

    override fun validateAccountBalance(user: User, basePrice: Long) {
//        if (user.getAccountBalance() < Math.floorDiv(basePrice, BusinessNumber.FEE_ADD_KOI_TO_AUCTION)) {
//            throw new MalformDataException("You don't have enough money to register Product to nocket");
//        }
    }

    override fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    override fun existsByPhoneNumber(phoneNumber: String): Boolean {
        return userRepository.existsByPhoneNumber(phoneNumber)
    }

    override fun existsById(id: UUID): Boolean {
        return userRepository.existsById(id)
    }

    override fun saveUser(user: User) {
        with(user){
            activityStatus = User.ActivityStatus.ONLINE
        }

        userRepository.save(user)
    }

    override fun disconnect(user: User) {
        val storedUser = userRepository.findById(user.id).orElse(null)
        if (storedUser != null) {
            storedUser.activityStatus = User.ActivityStatus.OFFLINE
            userRepository.save(storedUser)
        }
    }

    override fun findConnectedUsers(): List<User> {
        return userRepository.findAllByActivityStatus(User.ActivityStatus.ONLINE)
    }
}
