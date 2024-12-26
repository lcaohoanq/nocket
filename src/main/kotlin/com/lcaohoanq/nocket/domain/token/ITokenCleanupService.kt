package com.lcaohoanq.nocket.domain.token

interface ITokenCleanupService {
    fun cleanupExpiredTokens()
}