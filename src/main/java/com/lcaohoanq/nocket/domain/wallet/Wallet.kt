package com.lcaohoanq.nocket.domain.wallet

import com.fasterxml.jackson.annotation.JsonBackReference
import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.domain.user.User
import jakarta.persistence.*
import org.hibernate.envers.Audited

@Audited
@Entity
@Table(name = "wallets")
class Wallet : BaseEntity() {
    @JvmField
    @Column(name = "balance", nullable = false)
    var balance: Float? = null

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference // Prevent infinite loop when serializing
    var user: User? = null
}
