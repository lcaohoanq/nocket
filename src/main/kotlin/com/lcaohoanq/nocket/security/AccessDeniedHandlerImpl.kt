package com.lcaohoanq.nocket.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.lcaohoanq.nocket.api.ApiError
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.Map

@Component
class AccessDeniedHandlerImpl(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {

    @Throws(IOException::class)
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: AccessDeniedException
    ) {
        response.contentType = "application/json"
        response.status = HttpStatus.FORBIDDEN.value()

        val errorResponse = ApiError.errorBuilder<Any>()
            .message("Access Denied")
            .reason("You don't have permission to access this resource")
            .statusCode(HttpStatus.FORBIDDEN.value())
            .isSuccess(false)
            .data(
                Map.of(
                    "timestamp", System.currentTimeMillis(),
                    "path", request.requestURI
                )
            )
            .build()

        objectMapper.writeValue(response.outputStream, errorResponse)
    }
}