package com.lcaohoanq.nocket.domain.reaction

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.domain.post.Post
import com.lcaohoanq.nocket.domain.user.User
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "post_reactions")
class PostReaction : BaseEntity() {
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference
    var post: Post? = null

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference(value = "user-reactions")
    var user: User? = null

    @ManyToOne
    @JoinColumn(name = "reaction_id", nullable = false)
    @JsonManagedReference(value = "reaction-reactions")
    var reaction: Reaction? = null
}
