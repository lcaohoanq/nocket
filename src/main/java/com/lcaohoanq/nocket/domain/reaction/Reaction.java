package com.lcaohoanq.nocket.domain.reaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lcaohoanq.nocket.enums.EReaction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "reactions")
public class Reaction {

    @Id
    @SequenceGenerator(name = "reactions_seq", sequenceName = "reactions_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reactions_seq")
    @Column(name="id", unique=true, nullable=false)
    @JsonProperty("id")
    private Long id;
    
    @Enumerated(EnumType.ORDINAL)
    private EReaction reaction;


}
