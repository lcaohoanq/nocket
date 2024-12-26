package com.lcaohoanq.nocket.aop;

import com.lcaohoanq.nocket.enums.UserStatus;
import com.lcaohoanq.nocket.exception.UserHasBeenBannedException;
import com.lcaohoanq.nocket.exception.UserHasBeenVerifiedException;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.domain.user.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class EmailPhoneAspect {

    private final HttpServletRequest request;
    private final IUserService userService;

    @Before("execution(* com.lcaohoanq.nocket.domain.mail.MailController.*(..)) && args(toEmail,..) && !@annotation(com.lcaohoanq.nocket.annotation.SkipEmailValidation)")
    public void checkValidEmail(JoinPoint joinPoint, String toEmail) {
        User user = userService.findUserByEmail(toEmail);

        if (user.getStatus() == UserStatus.BANNED) {
            throw new UserHasBeenBannedException(String.format("User with email %s has been banned", toEmail));
        }

        if (user.getStatus() == UserStatus.VERIFIED) {
            throw new UserHasBeenVerifiedException(String.format("User with email %s has been verified", toEmail));
        }

        request.setAttribute("validatedEmail", user);
    }

    @Before("execution(* com.lcaohoanq.nocket.domain.auth.ForgotPasswordController.*(..)) && args(toEmail,..)")
    public void checkValidEmailForgotPassword(JoinPoint joinPoint, String toEmail) {
        User user = userService.findUserByEmail(toEmail);

        request.setAttribute("validatedEmail", user);
    }

}