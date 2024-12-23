package com.lcaohoanq.nocket.domain.chat

import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.metadata.MediaMeta
import jakarta.persistence.*
import org.hibernate.envers.Audited

@Audited
@Entity
@Table(name = "attachments")
@AttributeOverrides(
    AttributeOverride(
        name = "createdAt",
        column = Column(name = "attachment_created_at")
    ), AttributeOverride(name = "updatedAt", column = Column(name = "attachment_updated_at"))
)
class Attachment : BaseEntity() {
    @Embedded
    private val mediaMeta: MediaMeta? = null
}
