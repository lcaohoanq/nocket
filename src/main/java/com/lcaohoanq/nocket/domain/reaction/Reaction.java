package com.lcaohoanq.nocket.domain.reaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lcaohoanq.nocket.base.entity.BaseEntity;
import com.lcaohoanq.nocket.enums.EReaction;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
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
@Table(name = "reactions")
@SuperBuilder
public class Reaction extends BaseEntity {
    
    @Enumerated(EnumType.ORDINAL)
    @Column(unique = true, nullable = false)
    private EReaction reaction;

    @OneToMany(mappedBy = "reaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PostReaction> reactions = new ArrayList<>();
}
