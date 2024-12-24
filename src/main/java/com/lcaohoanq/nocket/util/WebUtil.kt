package com.lcaohoanq.nocket.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

object WebUtil {
    fun getCurrentRequest(): HttpServletRequest = (RequestContextHolder.currentRequestAttributes()
            as ServletRequestAttributes).request
}
