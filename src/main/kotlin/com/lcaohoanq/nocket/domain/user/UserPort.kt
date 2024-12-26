package com.lcaohoanq.nocket.domain.user

import com.fasterxml.jackson.annotation.*
import com.lcaohoanq.nocket.enums.Gender
import com.lcaohoanq.nocket.enums.UserRole
import com.lcaohoanq.nocket.enums.UserStatus
import jakarta.validation.constraints.Pattern
import java.time.LocalDateTime
import java.util.*

interface UserPort {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class UpdateUserDTO(
        @JsonProperty("name") val name: String,
        @JsonProperty("email") val email: String,
        @JsonProperty("phone_number") val phoneNumber: String,
        @JsonProperty("password") val password: String,
        @JsonProperty("confirm_password") val confirmPassword: String,  // in case user wants to change password
        @JsonProperty("address") val address: String,
        @JsonProperty("status")
        
        @Pattern(
            regexp = "ACTIVE|INACTIVE|VERIFIED|UNVERIFIED|BANNED",
            message = "Status must be either ONGOING, INACTIVE, VERIFIED, UNVERIFIED, BANNED"
        ) val status: String,
        
        @JsonProperty("date_of_birth") val dob: String,
        @JsonProperty("avatar") val avatar: String
    )

    @JsonPropertyOrder(
        "id",
        "email",
        "password",
        "name",
        "gender",
        "is_active",
        "status",
        "date_of_birth",
        "phone_number",
        "address",
        "avatar",
        "role",
        "wallet_id",
        "preferred_language",
        "preferred_currency",
        "last_login_timestamp",
        "created_at",
        "updated_at"
    )
    data class UserResponse(
        val id: UUID,
        val email: String,
        @JsonIgnore val password: String,
        val name: String,
        val gender: Gender,
        @JsonProperty("is_active") val isActive: Boolean,
        val status: UserStatus,
        @JsonProperty("date_of_birth") val dateOfBirth: String,
        @JsonProperty("phone_number") val phoneNumber: String,
        @JsonProperty("avatar") val avatar: String,
        @JsonProperty("role") val role: UserRole,
        @JsonProperty("wallet_id") @JsonIgnore val walletId: UUID,
        @JsonProperty("preferred_language") val preferredLanguage: String,
        @JsonProperty("preferred_currency") val preferredCurrency: String,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") @JsonProperty("last_login_timestamp") val lastLoginTimestamp: LocalDateTime,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") @JsonProperty("created_at") val createdAt: LocalDateTime,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") @JsonProperty("updated_at") val updatedAt: LocalDateTime
    )
}
