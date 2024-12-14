package com.lcaohoanq.nocket.domain.mail;

import com.lcaohoanq.nocket.domain.user.User;
import io.reactivex.rxjava3.core.Single;
import jakarta.mail.MessagingException;
import org.thymeleaf.context.Context;

public interface IMailService {
    void sendMail(String to, String subject, String templateName, Context context) throws MessagingException;
    Single<User> createEmailVerification(User user);
}
