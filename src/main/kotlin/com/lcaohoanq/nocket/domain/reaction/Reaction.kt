package com.lcaohoanq.nocket.domain.reaction

import com.fasterxml.jackson.annotation.JsonBackReference
import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.enums.EReaction
import jakarta.persistence.*
import org.hibernate.envers.Audited

@Audited
@Entity
@Table(name = "reactions")
class Reaction : BaseEntity() {
    @Enumerated(EnumType.ORDINAL)
    @Column(unique = true, nullable = false)
    private var reaction: EReaction? = null

    @OneToMany(mappedBy = "reaction", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonBackReference(value = "reaction-reactions") //    @JsonIgnore
    private val reactions: List<PostReaction> = ArrayList()
    
    fun getReaction(): EReaction? {
        return reaction
    }
    
    fun setReaction(reaction: EReaction?) {
        this.reaction = reaction
    }
}
