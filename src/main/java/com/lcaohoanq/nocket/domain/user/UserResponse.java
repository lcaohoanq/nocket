package com.lcaohoanq.nocket.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lcaohoanq.nocket.enums.Gender;
import com.lcaohoanq.nocket.enums.UserRole;
import com.lcaohoanq.nocket.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonPropertyOrder(
    {
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
    }
)
public record UserResponse(
    UUID id,
    String email,
    @JsonIgnore String password,
    String name,
    Gender gender,
    @JsonProperty("is_active") boolean isActive,
    UserStatus status,
    @JsonProperty("date_of_birth") String dateOfBirth,
    @JsonProperty("phone_number") String phoneNumber,
    @JsonProperty("avatar") String avatar,
    @JsonProperty("role") UserRole role,
    @JsonIgnore @JsonProperty("wallet_id") UUID walletId,
    @JsonProperty("preferred_language") String preferredLanguage,
    @JsonProperty("preferred_currency") String preferredCurrency,
    
    @JsonProperty("last_login_timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    LocalDateTime lastLoginTimestamp,
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    LocalDateTime createdAt,
    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    LocalDateTime updatedAt
) {

}
