package com.lcaohoanq.nocket.domain.auth;

import com.lcaohoanq.nocket.domain.user.UserPort;
import com.lcaohoanq.nocket.domain.user.User;
import java.util.UUID;

public interface IAuthService {

    User register(AuthPort.AccountRegisterDTO accountRegisterDTO) throws Exception;
    String login(String email, String password) throws Exception;
    UserPort.UserResponse getUserDetailsFromToken(String token) throws Exception;
    void logout(String token, User user) throws Exception;
    void verifyOtpToVerifyUser(UUID userId, String otp) throws Exception;
    void verifyOtpIsCorrect(UUID userId, String otp) throws Exception;
}
