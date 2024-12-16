package com.lcaohoanq.nocket.domain.wallet;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Wallet findByUserId(UUID user_id);
    
}
