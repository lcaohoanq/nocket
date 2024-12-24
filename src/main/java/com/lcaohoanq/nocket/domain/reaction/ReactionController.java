package com.lcaohoanq.nocket.domain.reaction;

import com.lcaohoanq.nocket.api.ApiResponse;
import com.lcaohoanq.nocket.base.exception.DataNotFoundException;
import com.lcaohoanq.nocket.enums.EReaction;
import com.lcaohoanq.nocket.mapper.ReactionMapper;
import java.util.List;
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
    private final ReactionMapper reactionMapper;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ReactionPort.ReactionResponse>>> getAllReactions() {
        return ResponseEntity.ok(
            ApiResponse.<List<ReactionPort.ReactionResponse>>builder()
                .message("Get all reactions success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(reactionRepository.findAll().stream().map(reactionMapper::toReactionResponse)
                          .toList())
                .build()
        );
    }

    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<ReactionPort.ReactionResponse>> getReactionById(
        @RequestParam UUID id
    ) {
        return ResponseEntity.ok(
            ApiResponse.<ReactionPort.ReactionResponse>builder()
                .message("Get reaction by id success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(reactionRepository.findById(id).map(reactionMapper::toReactionResponse)
                          .orElseThrow(
                              () -> new DataNotFoundException("Reaction not found")
                          ))
                .build()
        );
    }

    @PostMapping("/generate-reaction")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_MEMBER', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse<ReactionPort.ReactionResponse>> generateReaction(
        @RequestBody ReactionPort.ReactionDTO reaction
    ) {

        Reaction newReaction = new Reaction();
        newReaction.setReaction(reaction.getReaction());

        return ResponseEntity.ok(
            ApiResponse.<ReactionPort.ReactionResponse>builder()
                .message("Generate reaction success")
                .statusCode(HttpStatus.OK.value())
                .isSuccess(true)
                .data(reactionMapper.toReactionResponse(reactionRepository.save(newReaction)))
                .build()
        );
    }
}
