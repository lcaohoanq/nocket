package com.lcaohoanq.nocket.domain.post;

import com.lcaohoanq.nocket.domain.cache.IPostRedisService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostListener {

    private final IPostRedisService postRedisService;
    
    @PrePersist
    public void prePersist(Post post) {
        log.info("prePersist");
    }

    @PostPersist //save = persis
    public void postPersist(Post post) {
        // Update Redis cache
        log.info("postPersist");
        postRedisService.clear();
    }

    @PreUpdate
    public void preUpdate(Post post) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        log.info("preUpdate");
    }

    @PostUpdate
    public void postUpdate(Post post) {
        // Update Redis cache
        log.info("postUpdate");
        postRedisService.clear();
    }

    @PreRemove
    public void preRemove(Post post) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        log.info("preRemove");
    }

    @PostRemove
    public void postRemove(Post post) {
        // Update Redis cache
        log.info("postRemove");
        postRedisService.clear();
    }
    
}
