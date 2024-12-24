package com.lcaohoanq.nocket.enums

enum class EPaymentStatus(val status: String) {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    REFUNDED("REFUNDED"),
    ALL("ALL"); //for searching
}
