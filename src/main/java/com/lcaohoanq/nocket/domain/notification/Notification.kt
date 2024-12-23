package com.lcaohoanq.nocket.domain.notification

import com.lcaohoanq.nocket.base.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.envers.Audited

@Audited
@Entity
@Table(name = "notifications")
class Notification : BaseEntity()
