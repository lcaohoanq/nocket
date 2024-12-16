package com.lcaohoanq.nocket.domain.otp;

import java.util.Optional;
import java.util.UUID;

public interface IOtpService {

    Otp createOtp(Otp otp);
    void disableOtp(UUID id);
    Optional<Otp> getOtpByEmailOtp(String email, String otp);
    void setOtpExpired();
}
