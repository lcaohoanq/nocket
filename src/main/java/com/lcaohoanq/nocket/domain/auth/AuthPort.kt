package com.lcaohoanq.nocket.domain.auth

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.lcaohoanq.nocket.constant.Regex
import com.lcaohoanq.nocket.enums.Country
import com.lcaohoanq.nocket.enums.Currency
import com.lcaohoanq.nocket.enums.Gender
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

interface AuthPort {

    data class AccountRegisterDTO(
        @JsonProperty("name")
        @NotBlank(message = "Name is required") val name: String,

        @JsonProperty("email")
        @Email(message = "Email is invalid") val email: String,

        @JsonProperty("phone_number")
        @Pattern(regexp = Regex.PHONE_NUMBER_REGEX, message = "Phone number is invalid")
        @NotBlank(message = "Phone number is required") val phoneNumber: String,

        @JsonProperty("gender") val gender: Gender,

        @JsonProperty("password")
        @Pattern(
            regexp = Regex.PASSWORD_REGEX,
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        @NotBlank(message = "Password is required") val password: String,

        @JsonProperty("confirm_password")
        @NotBlank(message = "Confirm password is required") val confirmPassword: String,

        @JsonProperty("date_of_birth")
        val dateOfBirth: String,

        @JsonProperty("preferred_language")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        val preferredLanguage: Country,

        @JsonProperty("preferred_currency")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        val preferredCurrency: Currency
    )

    data class UpdatePasswordDTO(
        @JsonProperty("email")
        @Email(message = "Email must be a valid email")
        @NotBlank(message = "Email must not be blank")
        val email: String,

        @JsonProperty("new_password")
        @Pattern(
            regexp = Regex.PASSWORD_REGEX,
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        @NotBlank(message = "New password must not be blank")
        val newPassword: String
    )

    data class UserLoginDTO(
        @JsonProperty("email")
        @Email(message = "Email is not valid")
        @NotBlank(message = "Email is required")
        val email: String,
        @JsonProperty("password")
        @NotBlank(message = "Password is required")
        val password: String
    )

    data class VerifyUserDTO(
        @JsonProperty("email")
        @NotBlank(message = "Email is required") val email: String,
        @JsonProperty("otp")
        @NotBlank(message = "OTP is required") val otp: String
    )

}