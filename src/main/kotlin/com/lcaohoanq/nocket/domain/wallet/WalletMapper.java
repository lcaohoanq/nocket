package com.lcaohoanq.nocket.domain.wallet;

import com.lcaohoanq.nocket.domain.wallet.WalletPort.WalletResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletResponse toWalletResponse(Wallet wallet);

}