package com.lcaohoanq.nocket.domain.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lcaohoanq.nocket.base.entity.BaseEntity;
import com.lcaohoanq.nocket.domain.user.User;
import com.lcaohoanq.nocket.enums.PostType;
import com.lcaohoanq.nocket.metadata.MediaMeta;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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

    @Id
    @SequenceGenerator(name = "posts_seq", sequenceName = "posts_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_seq")
    @Column(name="id", unique=true, nullable=false)
    @JsonProperty("id")
    private Long id;

    private PostType postType;
    private String caption;
    
    @Embedded
    private MediaMeta mediaMeta;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    
}
