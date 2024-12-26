package com.lcaohoanq.nocket.domain.reaction

import com.lcaohoanq.nocket.enums.EReaction
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReactionRepository : JpaRepository<Reaction, UUID> {
    fun findByReaction(reactionType: EReaction): Optional<Reaction>
}
