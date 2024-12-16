package com.lcaohoanq.nocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EReaction {

    LIKE("👍"),
    HAHA("😂"),
    WOW("😮"),
    SAD("😢"),
    ANGRY("😠"),
    LOVE("❤️");

    private final String emoji;
    
}
