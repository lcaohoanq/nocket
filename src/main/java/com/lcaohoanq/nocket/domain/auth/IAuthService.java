package com.lcaohoanq.nocket.domain.auth;

import com.lcaohoanq.nocket.domain.user.UserResponse;
import com.lcaohoanq.nocket.domain.user.User;
import java.util.UUID;

public interface IAuthService {

    User register(AccountRegisterDTO accountRegisterDTO) throws Exception;
    String login(String email, String password) throws Exception;
    UserResponse getUserDetailsFromToken(String token) throws Exception;
    void logout(String token, User user) throws Exception;
    void verifyOtpToVerifyUser(UUID userId, String otp) throws Exception;
    void verifyOtpIsCorrect(UUID userId, String otp) throws Exception;
}
