package com.lcaohoanq.nocket.domain.reaction;

import com.lcaohoanq.nocket.enums.EReaction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {

    Optional<Reaction> findByReaction(EReaction reactionType);


}
