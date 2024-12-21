package com.lcaohoanq.nocket.domain.post

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.lcaohoanq.nocket.base.entity.BaseEntity
import com.lcaohoanq.nocket.constant.BusinessNumber
import com.lcaohoanq.nocket.constant.ValidationMessage
import com.lcaohoanq.nocket.domain.reaction.PostReaction
import com.lcaohoanq.nocket.domain.user.User
import com.lcaohoanq.nocket.enums.PostType
import com.lcaohoanq.nocket.metadata.MediaMeta
import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity
@Table(name = "posts")
@AttributeOverrides(
    AttributeOverride(
        name = "createdAt",
        column = Column(name = "post_created_at")
    ),
    AttributeOverride(name = "updatedAt", column = Column(name = "post_updated_at"))
)
@EntityListeners(PostListener::class)
class Post : BaseEntity() {
    @Column(name = "post_type")
    private var postType: PostType? = null

    @Size(
        max = BusinessNumber.MAXIMUM_POST_CAPTION_LENGTH,
        message = ValidationMessage.CAPTION_MAX_LENGTH_MSG
    )
    private var caption: String? = null

    @Embedded
    private var mediaMeta: MediaMeta? = null

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private var user: User? = null

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference(value = "post-reactions")
    private var reactions: MutableList<PostReaction> = ArrayList()

    // Setter methods
    fun setPostType(postType: PostType) {
        this.postType = postType
    }

    fun setCaption(caption: String) {
        this.caption = caption
    }

    fun setMediaMeta(mediaMeta: MediaMeta) {
        this.mediaMeta = mediaMeta
    }

    fun setUser(user: User) {
        this.user = user
    }

    fun setReactions(reactions: MutableList<PostReaction>) {
        this.reactions = reactions
    }

    fun getUser(): User? {
        return user
    }

    // Convenience methods
    fun addReaction(reaction: PostReaction) {
        reactions.add(reaction)
        reaction.post = this
    }
}
