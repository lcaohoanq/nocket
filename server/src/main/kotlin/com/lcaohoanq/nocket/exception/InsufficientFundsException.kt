package com.lcaohoanq.nocket.exception

import lombok.NoArgsConstructor

@NoArgsConstructor
class InsufficientFundsException(message: String?) : RuntimeException(message)
