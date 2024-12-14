package com.lcaohoanq.nocket.domain.cart;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lcaohoanq.nocket.domain.user.UserResponse;
import java.time.LocalDateTime;
import java.util.List;

@JsonPropertyOrder({
    "id",
    "total_quantity",
    "total_price",
    "user",
    "cart_items",
    "created_at",
    "updated_at"
})
public record CartResponse(
    @JsonProperty("id")
    Long id,

    @JsonProperty("total_quantity")
    Integer totalQuantity,

    Integer total,

    @JsonProperty("user")
    UserResponse user,

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    LocalDateTime createdAt,

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    LocalDateTime updatedAt
) {}
