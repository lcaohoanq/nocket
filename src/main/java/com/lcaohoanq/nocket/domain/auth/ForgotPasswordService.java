package com.lcaohoanq.nocket.domain.auth;

import com.lcaohoanq.nocket.constant.EmailSubject;
import com.lcaohoanq.nocket.enums.EmailCategoriesEnum;
import com.lcaohoanq.nocket.domain.otp.Otp;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.mail.IMailService;
import com.lcaohoanq.nocket.domain.otp.IOtpService;
import com.lcaohoanq.nocket.util.OtpUtil;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForgotPasswordService implements IForgotPasswordService {

    private final IMailService mailService;
    private final IOtpService otpService;

    @Override
    @Transactional
    public void sendEmailOtp(User existingUser) throws MessagingException {
        Context context = new Context();
        String otp = OtpUtil.generateOtp();
        context.setVariable("name", existingUser.getName());
        context.setVariable("otp", otp);

        mailService.sendMail(existingUser.getEmail(),
                             EmailSubject.subjectForgotPassword(existingUser.getName()),
                             EmailCategoriesEnum.FORGOT_PASSWORD.getType(),
                             context);

        Otp otpEntity = new Otp();
        otpEntity.setOtp(otp);
        otpEntity.setEmail(existingUser.getEmail());
        otpEntity.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        otpEntity.setUsed(false);
        otpEntity.setExpired(false);
        
        otpService.createOtp(otpEntity);
    }
}
