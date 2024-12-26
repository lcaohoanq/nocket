package com.lcaohoanq.nocket.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.lcaohoanq.nocket.api.ApiError
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.Map

@Component
class AuthenticationEntryPointImpl(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    
    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: AuthenticationException
    ) {
        response.contentType = "application/json"
        response.status = HttpStatus.UNAUTHORIZED.value()

        val errorResponse = ApiError.errorBuilder<Any>()
            .message("Authentication Required")
            .reason(ex.message)
            .statusCode(HttpStatus.UNAUTHORIZED.value())
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