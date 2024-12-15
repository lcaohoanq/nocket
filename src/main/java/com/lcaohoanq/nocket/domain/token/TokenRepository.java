package com.lcaohoanq.nocket.domain.token;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByUserId(Long userId);

    Token findByToken(String token);

    Token findByRefreshToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expirationDate < :now OR t.expired = true")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);


}