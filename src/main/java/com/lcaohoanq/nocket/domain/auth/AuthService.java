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
import com.lcaohoanq.nocket.domain.token.Token;
import com.lcaohoanq.nocket.domain.token.TokenService;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.UserPort;
import com.lcaohoanq.nocket.domain.user.UserPort.UserResponse;
import com.lcaohoanq.nocket.domain.user.UserRepository;
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
import com.lcaohoanq.nocket.mapper.TokenMapper;
import com.lcaohoanq.nocket.mapper.UserMapper;
import com.lcaohoanq.nocket.metadata.MediaMeta;
import com.lcaohoanq.nocket.util.Identifiable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService, Identifiable {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtTokenUtils jwtTokenUtils;
    SocialAccountRepository socialAccountRepository;
    LocalizationUtils localizationUtils;
    IMailService mailService;
    TokenService tokenService;
    OtpService otpService;
    UserService userService;
    WalletRepository walletRepository;
    UserMapper userMapper;
    TokenMapper tokenMapper;
    HttpServletRequest request;

    @Override
    @Transactional
    public User register(AuthPort.AccountRegisterDTO accountRegisterDTO) throws Exception {

        if (!accountRegisterDTO.getPassword().matches(Regex.PASSWORD_REGEX)) {
            throw new PasswordWrongFormatException(
                "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }

        if (!accountRegisterDTO.getPassword().equals(accountRegisterDTO.getConfirmPassword())) {
            throw new MalformBehaviourException("Password and confirm password must be the same");
        }

        String email = accountRegisterDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        if (userRepository.existsByPhoneNumber(accountRegisterDTO.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        HttpServletRequest request =
            ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
        String acceptLanguage = request.getHeader("Accept-Language");
        String preferredLanguage = String.valueOf(
            Optional.of(accountRegisterDTO.getPreferredLanguage())
                .orElse(acceptLanguage == null || acceptLanguage.isEmpty()
                            ? Country.UNITED_STATES
                            : Country.valueOf(acceptLanguage.toUpperCase())));

        String preferredCurrency = String.valueOf(
            Optional.of(accountRegisterDTO.getPreferredCurrency())
                .orElse(Currency.USD));

        return Single.fromCallable(() -> {
                // 1. First create the User without avatars
                User newUser = User.builder()
                    .name(accountRegisterDTO.getName())
                    .email(accountRegisterDTO.getEmail())
                    .password(passwordEncoder.encode(accountRegisterDTO.getPassword()))
                    .phoneNumber(accountRegisterDTO.getPhoneNumber())
                    .isActive(true)
                    .gender(accountRegisterDTO.getGender())
                    .status(UserStatus.UNVERIFIED)
                    .dateOfBirth(accountRegisterDTO.getDateOfBirth())
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
    public LoginResponse login(String email, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.WRONG_PHONE_PASSWORD));
        }

        User existingUser = optionalUser.get();

        existingUser.setLastLoginTimestamp(LocalDateTime.now());
        userRepository.save(existingUser);

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                email,
                password,
                existingUser.getAuthorities());

        authenticationManager.authenticate(authenticationToken);

        String token = jwtTokenUtils.generateToken(existingUser);

        UserResponse userDetail = getUserDetailsFromToken(token);

        Token jwtToken = tokenService.addToken(
            userDetail.getId(),
            token,
            isMobileDevice(request.getHeader("User-Agent")));

        log.info("New user logged in successfully");

        return new LoginResponse(
            tokenMapper.toTokenResponse(jwtToken),
            userDetail
        );
    }

    //Token
    @Override
    public UserPort.UserResponse getUserDetailsFromToken(String token) throws Exception {
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
        UserPort.UserResponse user = userService.findUserById(userId);

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
    }

}
