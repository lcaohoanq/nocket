package com.lcaohoanq.nocket.domain.reaction;

import com.lcaohoanq.nocket.enums.EReaction;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PostReactionDTO(
    @NotNull(message = "Post Id is required") UUID postId,
    EReaction reaction
) { }
