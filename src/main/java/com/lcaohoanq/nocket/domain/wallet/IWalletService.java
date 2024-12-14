package com.lcaohoanq.nocket.domain.wallet;

import com.lcaohoanq.nocket.domain.wallet.WalletDTO.WalletResponse;

public interface IWalletService {

    WalletResponse getByUserId(Long userId);
    void updateAccountBalance(Long userId, Long payment) throws Exception;
    
}
