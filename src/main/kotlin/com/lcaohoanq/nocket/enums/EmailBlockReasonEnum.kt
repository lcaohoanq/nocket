package com.lcaohoanq.nocket.enums

enum class EmailBlockReasonEnum (val reason: String) {
    ABUSE("Abuse activity"),
    SPAM("Spam activity"),
    OTHER("Other reason");
}
