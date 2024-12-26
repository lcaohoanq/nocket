package com.lcaohoanq.nocket.domain.token

import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.domain.user.User
import jakarta.persistence.*
import org.hibernate.envers.Audited
import java.time.LocalDateTime

@Audited
@Entity
@Table(name = "tokens")
class Token : BaseEntity() {

    @Column(name = "token", length = 255)
    var token: String? = null

    @Column(name = "refresh_token", length = 255)
    var refreshToken: String? = null

    @Column(name = "token_type", length = 50)
    var tokenType: String? = null

    @Column(name = "expiration_date")
    var expirationDate: LocalDateTime? = null

    @Column(name = "refresh_expiration_date")
    var refreshExpirationDate: LocalDateTime? = null

    @Column(name = "is_mobile")
    var isMobile: Boolean = false
    var revoked: Boolean? = false
    var expired = false

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null


}