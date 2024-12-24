package com.lcaohoanq.nocket.domain.reaction

import com.lcaohoanq.nocket.domain.post.Post
import com.lcaohoanq.nocket.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface PostReactionRepository : JpaRepository<PostReaction, UUID> {
    // Find reactions for a specific post
    fun findByPost(post: Post): List<PostReaction>

    fun findByPostId(postId: UUID): List<PostReaction>

    // Count reactions by type for a post
    @Query(
        ("SELECT r.reaction, COUNT(pr) FROM PostReaction pr " +
                "JOIN pr.reaction r WHERE pr.post = :post " +
                "GROUP BY r.reaction")
    )
    fun countReactionsByType(@Param("post") post: Post): List<Array<Any>>

    // Check if user has already reacted to a post
    fun findByPostAndUser(post: Post, user: User): Optional<PostReaction>
}
