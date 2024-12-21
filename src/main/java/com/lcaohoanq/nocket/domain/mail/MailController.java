package com.lcaohoanq.nocket.domain.mail;

import com.lcaohoanq.nocket.constant.EmailSubject;
import com.lcaohoanq.nocket.domain.otp.IOtpService;
import com.lcaohoanq.nocket.domain.otp.Otp;
import com.lcaohoanq.nocket.domain.user.IUserService;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.enums.EmailBlockReasonEnum;
import com.lcaohoanq.nocket.enums.EmailCategoriesEnum;
import com.lcaohoanq.nocket.util.OtpUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

@RequestMapping(path = "${api.prefix}/mail")
@RestController
@RequiredArgsConstructor
public class MailController {

    private final IMailService mailService;
    private final HttpServletRequest request;
    private final IOtpService otpService;
    private final IUserService userService;

    //api: /otp/send?type=email&recipient=abc@gmail
    public ResponseEntity<MailResponse> sendOtp(@RequestParam String toEmail)
        throws MessagingException {
        User user = (User) request.getAttribute("validatedEmail");

        String name = user.getName();
        Context context = new Context();
        String otp = OtpUtil.generateOtp();
        context.setVariable("name", name);
        context.setVariable("otp", otp);
        mailService.sendMail(toEmail, EmailSubject.subjectGreeting(name),
                             EmailCategoriesEnum.OTP.getType(),
                             context);
        MailResponse response = new MailResponse("Mail sent successfully");
        
        Otp otpEntity = new Otp();
        otpEntity.setOtp(otp);
        otpEntity.setEmail(toEmail);
        otpEntity.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        otpEntity.setUsed(false);
        otpEntity.setExpired(false);
        
        otpService.createOtp(otpEntity);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/block")
    ResponseEntity<MailResponse> sendBlockAccount(@RequestParam String toEmail)
        throws MessagingException {
        User user = (User) request.getAttribute("validatedEmail");
        Context context = new Context();
        context.setVariable("reason", EmailBlockReasonEnum.ABUSE.getReason());
        mailService.sendMail(toEmail, EmailSubject.subjectBlockEmail(user.getName()),
                             EmailCategoriesEnum.BLOCK_ACCOUNT.getType(), context);
        MailResponse response = new MailResponse("Mail sent successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/forgotPassword")
    ResponseEntity<MailResponse> sendForgotPassword(@RequestParam String toEmail)
        throws MessagingException {
        User user = (User) request.getAttribute("validatedEmail");
        String name = user.getName();
        Context context = new Context();
        String otp = OtpUtil.generateOtp();
        context.setVariable("name", name);
        context.setVariable("otp", otp);
        mailService.sendMail(toEmail, EmailSubject.subjectGreeting(name),
                             EmailCategoriesEnum.FORGOT_PASSWORD.getType(), context);
        MailResponse response = new MailResponse("Mail sent successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}