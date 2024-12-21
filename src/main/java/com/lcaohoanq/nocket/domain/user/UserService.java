package com.lcaohoanq.nocket.domain.user;

import com.lcaohoanq.nocket.api.PageResponse;
import com.lcaohoanq.nocket.base.exception.DataNotFoundException;
import com.lcaohoanq.nocket.component.JwtTokenUtils;
import com.lcaohoanq.nocket.component.LocalizationUtils;
import com.lcaohoanq.nocket.constant.MessageKey;
import com.lcaohoanq.nocket.domain.auth.AuthPort;
import com.lcaohoanq.nocket.domain.avatar.Avatar;
import com.lcaohoanq.nocket.domain.mail.IMailService;
import com.lcaohoanq.nocket.domain.otp.OtpService;
import com.lcaohoanq.nocket.domain.socialaccount.SocialAccount;
import com.lcaohoanq.nocket.domain.socialaccount.SocialAccountRepository;
import com.lcaohoanq.nocket.enums.EmailCategoriesEnum;
import com.lcaohoanq.nocket.enums.ProviderName;
import com.lcaohoanq.nocket.enums.UserRole;
import com.lcaohoanq.nocket.enums.UserStatus;
import com.lcaohoanq.nocket.exception.EmailAlreadyUsedException;
import com.lcaohoanq.nocket.exception.MalformBehaviourException;
import com.lcaohoanq.nocket.exception.MalformDataException;
import com.lcaohoanq.nocket.exception.PermissionDeniedException;
import com.lcaohoanq.nocket.exception.PhoneAlreadyUsedException;
import com.lcaohoanq.nocket.exception.UpdateEmailException;
import com.lcaohoanq.nocket.mapper.UserMapper;
import com.lcaohoanq.nocket.metadata.MediaMeta;
import com.lcaohoanq.nocket.util.PaginationConverter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService, PaginationConverter {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtTokenUtils jwtTokenUtils;
    SocialAccountRepository socialAccountRepository;
    LocalizationUtils localizationUtils;
    IMailService mailService;
    OtpService otpService;
    UserMapper userMapper;

    @Override
    public PageResponse<UserPort.UserResponse> fetchUser(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return mapPageResponse(
            usersPage,
            pageable,
            userMapper::toUserResponse,
            "Get all users successfully");
    }

    @Override
    public String loginOrRegisterGoogle(String email, String name, String googleId,
                                        String avatarUrl) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = null;
        SocialAccount socialAccount;

        if (optionalUser.isEmpty()) {
            // Register new user

            User newUser = User.builder()
                .name(name)
                .email(email)
                .avatars(
                    List.of(
                        Avatar.builder()
                            .mediaMeta(
                                MediaMeta.builder()
                                    .imageUrl(avatarUrl)
                                    .build()
                            )
                            .build()
                    )
                )
                .role(UserRole.MEMBER)
                .build();

            SocialAccount newSocialAccount = SocialAccount.builder()
                .providerName(ProviderName.GOOGLE)
                .name(name)
                .email(email)
                .build();

            user = userRepository.save(newUser);
            socialAccountRepository.save(newSocialAccount);
        }

        return jwtTokenUtils.generateToken(user);
    }

    @Override
    public UserPort.UserResponse findUserById(UUID id) throws DataNotFoundException {
        return userRepository.findById(id)
            .map(userMapper::toUserResponse)
            .orElseThrow(() -> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)));
    }

    @Override
    public User findUserByEmail(String email) throws DataNotFoundException {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new DataNotFoundException("User not found: " + email));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) throws DataNotFoundException {
        return userRepository.findByEmail(username)
            .orElseThrow(() -> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
            ));
    }

    @Transactional
    @Override
    public User updateUser(UUID userId, UserPort.UpdateUserDTO updatedUserDTO) throws Exception {
        // Find the existing user by userId
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
            ));

        // Check if the email is being changed and if it already exists for another user
        String newEmail = updatedUserDTO.getEmail();

        if (newEmail != null && !newEmail.isEmpty()) {
            // Check if the new email is different from the current user's email
            if (!newEmail.equals(existingUser.getEmail())) {
                // Check if the new email is already in use by another user
                Optional<User> userWithNewEmail = userRepository.findByEmail(newEmail);

                if (userWithNewEmail.isPresent()) {
                    throw new EmailAlreadyUsedException("This email address is already registered");
                }

                // If not, update the current user's email
                existingUser.setEmail(newEmail);
            }
            // If the email is the same as the current one, no changes are needed
        } else {
            throw new UpdateEmailException("This email cannot be empty");
        }

        // Check if the phoneNumber number is being changed and if it already exists for another user
        String newPhoneNumber = updatedUserDTO.getPhoneNumber();

        if (newPhoneNumber != null && !newPhoneNumber.isEmpty()) {
            // Check if the new phoneNumber number is different from the current user's phoneNumber number
            if (!newPhoneNumber.equals(existingUser.getPhoneNumber())) {
                // Check if the new phoneNumber number is already in use by another user
                Optional<User> userWithNewPhoneNumber = userRepository.findByPhoneNumber(
                    newPhoneNumber);

                if (userWithNewPhoneNumber.isPresent()) {
                    throw new PhoneAlreadyUsedException(
                        "This phoneNumber number is already registered");
                }

                // If not, update the current user's phoneNumber number
                existingUser.setPhoneNumber(newPhoneNumber);
            }
            // If the phoneNumber number is the same as the current one, no changes are needed
        } else {
            existingUser.setPhoneNumber(null);
        }

        // Update user information based on the DTO
        if (updatedUserDTO.getName() != null) {
            existingUser.setName(updatedUserDTO.getName());
        }
        if (updatedUserDTO.getStatus() != null) {
            existingUser.setStatus(UserStatus.valueOf(updatedUserDTO.getStatus()));
        }
        if (updatedUserDTO.getDob() != null) {
            existingUser.setDateOfBirth(updatedUserDTO.getDob());
        }
//        if (updatedUserDTO.avatar() != null) {
//            existingUser.setAvatars(updatedUserDTO.avatar());
//        }

        // Update the password if it is provided in the DTO
        if (updatedUserDTO.getPassword() != null
            && !updatedUserDTO.getPassword().isEmpty()) {
            if (!updatedUserDTO.getPassword().equals(updatedUserDTO.getConfirmPassword())) {
                throw new DataNotFoundException("Password and confirm password must be the same");
            }
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        //existingUser.setRole(updatedRole);
        // Save the updated user
        return userRepository.save(existingUser);
    }

    @Transactional
    @Override
    public User updateUserBalance(UUID userId, Long payment) throws Exception {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new DataNotFoundException(
//                        localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
//                ));
//
//        if (user.getAccountBalance() < payment) {
//            throw new BiddingRuleException("Not enough money to make payment");
//        }
//
//        user.setAccountBalance(user.getAccountBalance() - payment);
//
//        return userRepository.save(user);
        return null;
    }

    @Override
    public void bannedUser(UUID userId) throws DataNotFoundException {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
            ));

        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updatePassword(AuthPort.UpdatePasswordDTO updatePasswordDTO) throws Exception {
        User existingUser = userRepository.findByEmail(updatePasswordDTO.getEmail())
            .orElseThrow(() -> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
            ));

        if (!existingUser.isActive()) {
            throw new MalformBehaviourException(MessageKey.USER_NOT_FOUND);
        }

        if (existingUser.getStatus() != UserStatus.VERIFIED) {
            throw new MalformBehaviourException("User do not verified their account");
        }

        if (existingUser.getRole() == UserRole.MANAGER) {
            throw new PermissionDeniedException("Cannot change password for this account");
        }

        existingUser.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));

        mailService.sendMail(
            existingUser.getEmail(),
            "Password updated successfully",
            EmailCategoriesEnum.RESET_PASSWORD.getType(),
            new Context()
        );

        userRepository.save(existingUser);
    }

    //need send email
    @Override
    @Transactional
    public void softDeleteUser(UUID userId) throws DataNotFoundException {
        UserPort.UserResponse existingUser = findUserById(userId);
        if (!existingUser.isActive()) {
            throw new MalformDataException("User is already deleted");
        }
        userRepository.softDeleteUser(userId);
    }

    @Override
    @Transactional
    public void restoreUser(UUID userId) throws DataNotFoundException {
        UserPort.UserResponse existingUser = findUserById(userId);
        if (existingUser.isActive()) {
            throw new MalformDataException("User is already active");
        }
        userRepository.restoreUser(userId);
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) throws Exception {
        return null;
    }

    @Override
    @Transactional
    public void blockOrEnable(UUID userId, Boolean active) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
            ));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }

    @Override
    public void validateAccountBalance(User user, long basePrice) {
//        if (user.getAccountBalance() < Math.floorDiv(basePrice, BusinessNumber.FEE_ADD_KOI_TO_AUCTION)) {
//            throw new MalformDataException("You don't have enough money to register Product to nocket");
//        }
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public Boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

}
