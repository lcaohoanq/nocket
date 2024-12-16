package com.lcaohoanq.nocket.domain.post;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lcaohoanq.nocket.base.entity.BaseEntity;
import com.lcaohoanq.nocket.domain.reaction.PostReaction;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.enums.PostType;
import com.lcaohoanq.nocket.metadata.MediaMeta;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
@SuperBuilder
@AttributeOverrides({
    @AttributeOverride(name = "createdAt", column = @Column(name = "post_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "post_updated_at"))
})
public class Post extends BaseEntity {

    private PostType postType;
    private String caption;
    
    @Embedded
    private MediaMeta mediaMeta;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PostReaction> reactions = new ArrayList<>();

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> comments = new ArrayList<>();

    // Convenience methods
    public void addReaction(PostReaction reaction) {
        reactions.add(reaction);
        reaction.setPost(this);
    }

//    public void addComment(Comment comment) {
//        comments.add(comment);
//        comment.setPost(this);
//    }
    
}
