package com.lcaohoanq.nocket.domain.wallet

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*
import org.springframework.data.repository.query.Param

interface WalletRepository : JpaRepository<Wallet, Long> {
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    fun findByUserId(@Param("userId") userId: UUID): Wallet
}
