package com.lcaohoanq.nocket.domain.token;

import com.lcaohoanq.nocket.base.exception.DataNotFoundException;
import com.lcaohoanq.nocket.domain.user.User;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service

public interface ITokenService {
    Token addToken(UUID userId, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws Exception;
    void deleteToken(String token, User user) throws DataNotFoundException;
    Token findUserByToken(String token) throws DataNotFoundException;
}
