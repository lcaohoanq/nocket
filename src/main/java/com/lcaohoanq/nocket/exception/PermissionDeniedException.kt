package com.lcaohoanq.nocket.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class PermissionDeniedException : RuntimeException {
    val reason: String
    val path: String?

    constructor(message: String) : super(message) {
        this.reason = message
        this.path = null
    }

    constructor(message: String, path: String?) : super(message) {
        this.reason = message
        this.path = path
    }
}
