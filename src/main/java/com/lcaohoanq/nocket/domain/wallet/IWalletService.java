package com.lcaohoanq.nocket.domain.wallet;

import com.lcaohoanq.nocket.domain.wallet.WalletDTO.WalletResponse;
import java.util.UUID;

public interface IWalletService {

    WalletResponse getByUserId(UUID userId);
    void updateAccountBalance(UUID userId, Long payment) throws Exception;
    
}
