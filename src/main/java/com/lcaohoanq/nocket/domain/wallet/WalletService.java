package com.lcaohoanq.nocket.domain.wallet;

import com.lcaohoanq.nocket.base.exception.DataNotFoundException;
import com.lcaohoanq.nocket.component.LocalizationUtils;
import com.lcaohoanq.nocket.constant.MessageKey;
import com.lcaohoanq.nocket.domain.mail.IMailService;
import com.lcaohoanq.nocket.domain.user.IUserService;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.UserRepository;
import com.lcaohoanq.nocket.domain.user.UserResponse;
import com.lcaohoanq.nocket.domain.wallet.WalletDTO.WalletResponse;
import com.lcaohoanq.nocket.enums.EmailCategoriesEnum;
import com.lcaohoanq.nocket.mapper.WalletMapper;
import jakarta.mail.MessagingException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService implements IWalletService {

    private final WalletRepository walletRepository;
    private final IUserService userService;
    private final UserRepository userRepository;
    private final LocalizationUtils localizationUtils;
    private final IMailService mailService;
    private final WalletMapper walletMapper;

    @Override
    public WalletResponse getByUserId(UUID userId) {
        UserResponse existedUser = userService.findUserById(userId);
        return walletMapper.toWalletResponse(walletRepository.findByUserId(existedUser.id()));
    }

    @Transactional
    @Retryable(
        retryFor = {MessagingException.class},  // Retry only for specific exceptions
        maxAttempts = 3,                       // Maximum retry attempts
        backoff = @Backoff(delay = 2000)       // 2 seconds delay between retries
    )
    @Override
    public void updateAccountBalance(UUID userId, Long payment) throws Exception {
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(
                localizationUtils.getLocalizedMessage(MessageKey.USER_NOT_FOUND)
            ));
        existingUser.getWallet().setBalance(existingUser.getWallet().getBalance() + payment);

        Context context = new Context();
        context.setVariable("name", existingUser.getName());
        context.setVariable("amount", payment);
        context.setVariable("balance", existingUser.getWallet().getBalance());

        try {
            mailService.sendMail(
                existingUser.getEmail(),
                "Account balance updated",
                EmailCategoriesEnum.BALANCE_FLUCTUATION.getType(),
                context
            );
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", existingUser.getEmail(), e);
            throw new MessagingException(String.format("Failed to send email to %s", existingUser.getEmail()));
        }

        log.info("User {} balance updated. New balance: {}", userId, existingUser.getWallet().getBalance());
        userRepository.save(existingUser);
    }
    
}
