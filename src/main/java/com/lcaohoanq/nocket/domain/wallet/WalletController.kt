package com.lcaohoanq.nocket.domain.wallet

import com.lcaohoanq.nocket.api.ApiResponse
import com.lcaohoanq.nocket.constant.ApiConstant
import com.lcaohoanq.nocket.domain.user.IUserService
import com.lcaohoanq.nocket.domain.wallet.WalletPort.WalletResponse
import com.lcaohoanq.nocket.exception.MalformDataException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("${ApiConstant.API_PREFIX}/wallets")
class WalletController(
    private val walletService: IWalletService,
    private val userService: IUserService
) {

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    @GetMapping("/users")
    fun getWalletByUserId(): ResponseEntity<ApiResponse<WalletResponse>> {

        val userDetails = SecurityContextHolder.getContext()
            .authentication.principal as UserDetails
        val user =
            userService!!.findByUsername(userDetails.username)

        return ResponseEntity.ok().body(
            ApiResponse.builder<WalletResponse>()
                .message("Get wallet by user id successfully")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(walletService!!.getByUserId(user.id))
                .build()
        )
    }

    //     PUT: localhost:4000/api/v1/users/4/deposit/100
    //     Header: Authorization Bearer token
    @PutMapping("/{userId}/deposit/{payment}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    @Throws(Exception::class)
    fun deposit(
        @PathVariable userId: UUID,
        @PathVariable payment: Long
    ): ResponseEntity<ApiResponse<*>> {
        if (payment <= 0) {
            throw MalformDataException("Payment must be greater than 0.")
        }

        walletService!!.updateAccountBalance(userId, payment)

        return ResponseEntity.ok().body(
            ApiResponse.builder<Void>()
                .message("Deposit successfully")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .build()
        )
    }
}
