package com.lcaohoanq.nocket.domain.auth;

import com.lcaohoanq.nocket.base.exception.DataNotFoundException;
import com.lcaohoanq.nocket.component.JwtTokenUtils;
import com.lcaohoanq.nocket.component.LocalizationUtils;
import com.lcaohoanq.nocket.constant.MessageKey;
import com.lcaohoanq.nocket.constant.Regex;
import com.lcaohoanq.nocket.domain.avatar.Avatar;
import com.lcaohoanq.nocket.domain.mail.IMailService;
import com.lcaohoanq.nocket.domain.otp.Otp;
import com.lcaohoanq.nocket.domain.otp.OtpService;
import com.lcaohoanq.nocket.domain.socialaccount.SocialAccountRepository;
import com.lcaohoanq.nocket.domain.token.TokenService;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.UserRepository;
import com.lcaohoanq.nocket.domain.user.UserResponse;
import com.lcaohoanq.nocket.domain.user.UserService;
import com.lcaohoanq.nocket.domain.wallet.Wallet;
import com.lcaohoanq.nocket.domain.wallet.WalletRepository;
import com.lcaohoanq.nocket.enums.Country;
import com.lcaohoanq.nocket.enums.Currency;
import com.lcaohoanq.nocket.enums.UserRole;
import com.lcaohoanq.nocket.enums.UserStatus;
import com.lcaohoanq.nocket.exception.ExpiredTokenException;
import com.lcaohoanq.nocket.exception.MalformBehaviourException;
import com.lcaohoanq.nocket.exception.PasswordWrongFormatException;
import com.lcaohoanq.nocket.mapper.UserMapper;
import com.lcaohoanq.nocket.metadata.MediaMeta;
import com.lcaohoanq.nocket.util.UUIDv7;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final SocialAccountRepository socialAccountRepository;
    private final LocalizationUtils localizationUtils;
    private final IMailService mailService;
    private final TokenService tokenService;
    private final OtpService otpService;
    private final UserService userService;
    private final WalletRepository walletRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User register(AccountRegisterDTO accountRegisterDTO) throws Exception {

        if (!accountRegisterDTO.password().matches(Regex.PASSWORD_REGEX)) {
            throw new PasswordWrongFormatException(
                "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }

        if (!accountRegisterDTO.password().equals(accountRegisterDTO.confirmPassword())) {
            throw new MalformBehaviourException("Password and confirm password must be the same");
        }

        String email = accountRegisterDTO.email();
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        if (userRepository.existsByPhoneNumber(accountRegisterDTO.phoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        HttpServletRequest request =
            ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
        String acceptLanguage = request.getHeader("Accept-Language");
        String preferredLanguage = String.valueOf(
            Optional.ofNullable(accountRegisterDTO.preferredLanguage())
                .orElse(acceptLanguage == null || acceptLanguage.isEmpty()
                            ? Country.UNITED_STATES
                            : Country.valueOf(acceptLanguage.toUpperCase())));

        String preferredCurrency = String.valueOf(
            Optional.ofNullable(accountRegisterDTO.preferredCurrency())
                .orElse(Currency.USD));

        return Single.fromCallable(() -> {
                // 1. First create the User without avatars
                User newUser = User.builder()
                    .name(accountRegisterDTO.name())
                    .email(accountRegisterDTO.email())
                    .password(passwordEncoder.encode(accountRegisterDTO.password()))
                    .phoneNumber(accountRegisterDTO.phoneNumber())
                    .isActive(true)
                    .gender(accountRegisterDTO.gender())
                    .status(UserStatus.UNVERIFIED)
                    .dateOfBirth(accountRegisterDTO.dateOfBirth())
                    .preferredLanguage(preferredLanguage)
                    .preferredCurrency(preferredCurrency)
                    .role(UserRole.MEMBER)
                    .build();

                // 2. Save the user first
                newUser = userRepository.save(newUser);

                // 3. Create and set the avatar with the saved user
                Avatar avatar = Avatar.builder()
                    .mediaMeta(
                        MediaMeta.builder()
                            .imageUrl(
                                "https://www.shutterstock.com/image-vector/default-avatar-profile-icon-social-600nw-1677509740.jpg")
                            .build()
                    )
                    .user(newUser)  // Set the user reference
                    .build();

                // 4. Add avatar to user's avatar list
                newUser.setAvatars(List.of(avatar));
                
                // 5. Create and save the wallet
                Wallet newWallet = Wallet.builder()
                    .balance(0F)
                    .user(newUser)
                    .build();
                
                newWallet = walletRepository.save(newWallet);
                newUser.setWallet(newWallet);

                // 6. Save everything
                return userRepository.save(newUser);
            })
            .flatMap(mailService::createEmailVerification)
            .subscribeOn(Schedulers.io())
            .blockingGet();
    }

    @Override
    public String login(String email, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.WRONG_PHONE_PASSWORD));
        }
        User existingUser = optionalUser.get();

        existingUser.setLastLoginTimestamp(LocalDateTime.now());
        userRepository.save(existingUser);

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(email, password, existingUser.getAuthorities());
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(existingUser);
    }

    //Token
    @Override
    public UserResponse getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String email = jwtTokenUtils.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return userMapper.toUserResponse(user.get());
        } else {
            throw new Exception(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
            );
        }
    }

    @Override
    public void logout(String token, User user) throws Exception {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }

        tokenService.deleteToken(token, user);
    }

    @Transactional
    @Override
    public void verifyOtpToVerifyUser(UUID userId, String otp) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
            ));

        if (user.getStatus() == UserStatus.VERIFIED) {
            throw new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.USER_ALREADY_VERIFIED)
            );
        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new DataNotFoundException("User is banned");
        }

        Otp otpEntity = getOtpByEmailOtp(user.getEmail(), otp);

        //check the otp is expired or not
        if (otpEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            otpEntity.setExpired(true);
            otpService.disableOtp(otpEntity.getId());
            throw new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.OTP_EXPIRED)
            );
        }

        if (!otpEntity.getOtp().equals(String.valueOf(otp))) {
            throw new DataNotFoundException("Invalid OTP");
        }

        otpEntity.setUsed(true);
        otpService.disableOtp(otpEntity.getId());
        user.setStatus(UserStatus.VERIFIED);
        userRepository.save(user);
    }

    private Otp getOtpByEmailOtp(String email, String otp) {
        return otpService.getOtpByEmailOtp(email, otp)
            .orElseThrow(
                () -> new DataNotFoundException("OTP is not correct, please try again later"));
    }

    @Transactional
    @Override
    public void verifyOtpIsCorrect(UUID userId, String otp) throws Exception {
        UserResponse user = userService.findUserById(userId);

        Otp otpEntity = getOtpByEmailOtp(user.email(), otp);

        //check the otp is expired or not
        if (otpEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            otpEntity.setExpired(true);
            otpService.disableOtp(otpEntity.getId());
            throw new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.OTP_EXPIRED)
            );
        }

        if (!otpEntity.getOtp().equals(String.valueOf(otp))) {
            throw new DataNotFoundException("Invalid OTP");
        }

        otpEntity.setUsed(true);
        otpService.disableOtp(otpEntity.getId());
    }

}
