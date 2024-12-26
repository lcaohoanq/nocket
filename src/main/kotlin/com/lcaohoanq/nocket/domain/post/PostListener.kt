package com.lcaohoanq.nocket.domain.post

import com.lcaohoanq.nocket.domain.cache.IPostRedisService
import jakarta.persistence.*
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Slf4j
@Component
class PostListener(
    private val postRedisService: IPostRedisService
) {

    private val logger = KotlinLogging.logger {}

    @PrePersist
    fun prePersist(post: Post?) {
        logger.info("prePersist")
    }

    @PostPersist //save = persis
    fun postPersist(post: Post?) {
        // Update Redis cache
        logger.info("postPersist")
        postRedisService.clear()
    }

    @PreUpdate
    fun preUpdate(post: Post?) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        logger.info("preUpdate")
    }

    @PostUpdate
    fun postUpdate(post: Post?) {
        // Update Redis cache
        logger.info("postUpdate")
        postRedisService.clear()
    }

    @PreRemove
    fun preRemove(post: Post?) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        logger.info("preRemove")
    }

    @PostRemove
    fun postRemove(post: Post?) {
        // Update Redis cache
        logger.info("postRemove")
        postRedisService.clear()
    }
}
