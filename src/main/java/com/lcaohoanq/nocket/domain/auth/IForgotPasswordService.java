package com.lcaohoanq.nocket.domain.auth;

import com.lcaohoanq.nocket.domain.user.User;
import jakarta.mail.MessagingException;

public interface IForgotPasswordService {

    void sendEmailOtp(User existingUser) throws MessagingException;

}
