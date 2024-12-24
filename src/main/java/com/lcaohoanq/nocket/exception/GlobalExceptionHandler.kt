package com.lcaohoanq.nocket.exception

import com.lcaohoanq.nocket.api.ApiError
import com.lcaohoanq.nocket.base.exception.DataAlreadyExistException
import com.lcaohoanq.nocket.base.exception.DataNotFoundException
import com.lcaohoanq.nocket.base.exception.DataWrongFormatException
import com.lcaohoanq.nocket.base.exception.OutOfStockException
import com.lcaohoanq.nocket.component.LocalizationUtils
import com.lcaohoanq.nocket.constant.MessageKey
import io.jsonwebtoken.ExpiredJwtException
import jakarta.mail.MessagingException
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import java.util.*

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler(
    private val localizationUtils: LocalizationUtils
) {
    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(DataNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleDataNotFoundException(e: DataNotFoundException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage(MessageKey.DATA_NOT_FOUND))
            .reason(e.message)
            .statusCode(HttpStatus.NOT_FOUND.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(OutOfStockException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleOutOfStockException(e: OutOfStockException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message("Out of stock")
            .reason(e.message)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(IOException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleIOException(e: IOException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage(MessageKey.INTERNAL_SERVER_ERROR))
            .reason(e.message)
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(FileTooLargeException::class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    fun handleFileTooLargeException(e: FileTooLargeException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage(MessageKey.UPLOAD_IMAGES_FILE_LARGE))
            .reason(e.message)
            .statusCode(HttpStatus.PAYLOAD_TOO_LARGE.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(UnsupportedMediaTypeException::class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    fun handleUnsupportedMediaTypeException(e: UnsupportedMediaTypeException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage(MessageKey.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE))
            .reason(e.message)
            .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(MessagingException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMessagingException(e: MessagingException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message("Error sending email")
            .reason(e.message)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(NullPointerException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNullPointerException(e: NullPointerException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage(MessageKey.EXCEPTION_NULL_POINTER))
            .reason(e.message)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception): ApiError<Any> {
        logger.error("Internal server error: ", e)
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage(MessageKey.INTERNAL_SERVER_ERROR))
            .reason(e.message)
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(GenerateDataException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenerateDataException(e: GenerateDataException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage("exception.generate_data_error"))
            .reason(e.message)
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(InvalidApiPathVariableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidApiPathVariableException(
        e: InvalidApiPathVariableException
    ): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(
                localizationUtils.getLocalizedMessage(
                    MessageKey.EXCEPTION_INVALID_API_PATH_VARIABLE
                )
            )
            .reason(e.message)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException
    ): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message("JSON parse error")
            .reason(ex.message)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .isSuccess(false)
            .build()
    }


    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseBody
    @ResponseStatus(
        HttpStatus.BAD_REQUEST
    )
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<Map<String, String>> {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage: String = error.getDefaultMessage()?.toString() ?: ""
            errors[fieldName] = errorMessage
        }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MalformDataException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMalformDataException(e: MalformDataException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage(MessageKey.EXCEPTION_MALFORMED_DATA))
            .reason(e.message)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(MalformBehaviourException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMalformBehaviourException(e: MalformBehaviourException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(
                localizationUtils.getLocalizedMessage(MessageKey.EXCEPTION_MALFORMED_BEHAVIOUR)
            )
            .reason(e.message)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(
        e: AccessDeniedException?,
        request: WebRequest
    ): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage(MessageKey.EXCEPTION_PERMISSION_DENIED))
            .reason("Insufficient privileges to access this resource")
            .statusCode(HttpStatus.FORBIDDEN.value())
            .isSuccess(false)
            .data(
                java.util.Map.of(
                    "timestamp", System.currentTimeMillis(),
                    "path", (request as ServletWebRequest).request.requestURI
                )
            )
            .build()
    }

    @ExceptionHandler(JwtAuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleJwtAuthenticationException(
        e: JwtAuthenticationException,
        request: WebRequest
    ): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message("Authentication Failed")
            .reason(e.message)
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .isSuccess(false)
            .data(
                java.util.Map.of(
                    "timestamp", System.currentTimeMillis(),
                    "path", (request as ServletWebRequest).request.requestURI
                )
            )
            .build()
    }

    @ExceptionHandler(ExpiredJwtException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleExpiredJwtException(e: ExpiredJwtException?, request: WebRequest): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message("Token Expired")
            .reason("The provided authentication token has expired")
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .isSuccess(false)
            .data(
                java.util.Map.of(
                    "timestamp", System.currentTimeMillis(),
                    "path", (request as ServletWebRequest).request.requestURI
                )
            )
            .build()
    }

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadCredentialsException(e: BadCredentialsException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage(MessageKey.EXCEPTION_BAD_CREDENTIALS))
            .reason(e.message)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(DataWrongFormatException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handlePasswordWrongFormatException(e: DataWrongFormatException): ApiError<Any> {
        return ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage(MessageKey.WRONG_FORMAT))
            .reason(e.message)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .isSuccess(false)
            .build()
    }

    @ExceptionHandler(DataAlreadyExistException::class)
    fun handleDataAlreadyExistException(
        ex: DataAlreadyExistException
    ): ResponseEntity<ApiError<Any>> {
        val apiError = ApiError.errorBuilder<Any>()
            .message(ex.message)
            .statusCode(HttpStatus.CONFLICT.value())
            .isSuccess(false)
            .data(
                java.util.Map.of(
                    "timestamp", System.currentTimeMillis(),
                    "path", Objects.requireNonNull(
                        ServletUriComponentsBuilder.fromCurrentRequest().build().path
                    )
                )
            )
            .build()

        return ResponseEntity(apiError, HttpStatus.CONFLICT)
    }

    // Handle Spring's DataIntegrityViolationException separately
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        ex: DataIntegrityViolationException
    ): ResponseEntity<ApiError<Any>> {
        logger.error("DataIntegrityViolationException: ", ex)
        val apiError = ApiError.errorBuilder<Any>()
            .message(localizationUtils.getLocalizedMessage("exception.data_integrity_violation"))
            .reason(ex.mostSpecificCause.message)
            .statusCode(HttpStatus.CONFLICT.value())
            .isSuccess(false)
            .data(
                java.util.Map.of(
                    "timestamp", System.currentTimeMillis(),
                    "path", Objects.requireNonNull(
                        ServletUriComponentsBuilder.fromCurrentRequest().build().path
                    )
                )
            )
            .build()

        return ResponseEntity(apiError, HttpStatus.CONFLICT)
    }
}
