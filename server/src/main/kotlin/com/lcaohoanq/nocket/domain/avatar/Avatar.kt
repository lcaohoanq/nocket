package com.lcaohoanq.nocket.domain.avatar

import com.fasterxml.jackson.annotation.JsonBackReference
import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.metadata.MediaMeta
import jakarta.persistence.*
import org.hibernate.envers.Audited

@Audited
@Entity
@Table(name = "avatars")
@AttributeOverrides(
    AttributeOverride(
        name = "createdAt",
        column = Column(name = "avatar_created_at")
    ),
    AttributeOverride(
        name = "updatedAt",
        column = Column(name = "avatar_updated_at")
    )
)
class Avatar : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    var user: User? = null
    
    @Embedded
    var mediaMeta: MediaMeta? = null
}
