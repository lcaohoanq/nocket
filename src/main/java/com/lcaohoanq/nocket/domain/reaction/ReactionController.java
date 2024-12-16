package com.lcaohoanq.nocket.domain.reaction;

import com.lcaohoanq.nocket.api.ApiResponse;
import com.lcaohoanq.nocket.enums.EReaction;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/reactions")
@RequiredArgsConstructor
public class ReactionController {
    
    private final ReactionRepository reactionRepository;
    
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllReactions() {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .message("Get all reactions success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(reactionRepository.findAll())
                .build()
        );
    }
    
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<?>> getReactionById(
        @RequestParam UUID id
    ) {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .message("Get reaction by id success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(reactionRepository.findById(id))
                .build()
        );
    }
    
    @PostMapping("/generate-reaction")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<?>> generateReaction(
        @RequestBody ReactionDTO reaction
    ) {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .message("Generate reaction success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(reactionRepository.save(
                    Reaction.builder()
                        .reaction(reaction.reaction)
                        .build()
                ))
                .build()
        );
    }
    
    public record ReactionDTO(EReaction reaction) {}
    
}
