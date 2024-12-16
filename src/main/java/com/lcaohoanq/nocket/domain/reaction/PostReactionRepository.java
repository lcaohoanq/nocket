package com.lcaohoanq.nocket.domain.reaction;

import com.lcaohoanq.nocket.domain.post.Post;
import com.lcaohoanq.nocket.domain.user.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostReactionRepository extends JpaRepository<PostReaction, UUID> {
    // Find reactions for a specific post
    List<PostReaction> findByPost(Post post);

    // Count reactions by type for a post
    @Query("SELECT r.reaction, COUNT(pr) FROM PostReaction pr " +
        "JOIN pr.reaction r WHERE pr.post = :post " +
        "GROUP BY r.reaction")
    List<Object[]> countReactionsByType(@Param("post") Post post);

    // Check if user has already reacted to a post
    Optional<PostReaction> findByPostAndUser(Post post, User user);
}
