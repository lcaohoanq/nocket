package com.lcaohoanq.nocket.domain.otp

import com.fasterxml.jackson.annotation.JsonProperty
import com.lcaohoanq.nocket.base.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.envers.Audited
import java.time.LocalDateTime

@Audited
@Entity
@Table(name = "otps")
class Otp : BaseEntity() {
    @Column(name = "email")
    var email: String? = null

    @Column(name = "otp")
    var otp: String? = null

    @JsonProperty("expired_at")
    @Column(name = "expired_at")
    var expiredAt: LocalDateTime? = null // in milliseconds

    @JsonProperty("is_used")
    @Column(name = "is_used")
    var isUsed: Boolean = false

    @JsonProperty("is_expired")
    @Column(name = "is_expired")
    var isExpired: Boolean = false
}
