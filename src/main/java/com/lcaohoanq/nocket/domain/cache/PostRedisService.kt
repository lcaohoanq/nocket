package com.lcaohoanq.nocket.domain.cache

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.lcaohoanq.nocket.domain.post.PostPort.PostResponse
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
@RequiredArgsConstructor
class PostRedisService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val redisObjectMapper: ObjectMapper
) : IPostRedisService {

    @Value("\${spring.data.redis.use-redis-cache}")
    private val useRedisCache = false

    private fun getKeyFromGetAllPosts(pageRequest: PageRequest): String {
        val pageNumber = pageRequest.pageNumber
        val pageSize = pageRequest.pageSize
        val sort = pageRequest.sort
        val sortDirection = if (Objects.requireNonNull(sort.getOrderFor("id"))
                .direction == Sort.Direction.ASC
        ) "asc" else "desc"
        return String.format("all_posts:%d:%d:%s", pageNumber, pageSize, sortDirection)
    }

    @Throws(JsonProcessingException::class)
    override fun getAllPosts(pageRequest: PageRequest): List<PostResponse>? {
        if (!useRedisCache) {
            return null
        }
        val key = this.getKeyFromGetAllPosts(pageRequest)
        val json = redisTemplate.opsForValue().get(key) as String?

        return if (json != null) {
            redisObjectMapper.readValue(json, object : TypeReference<List<PostResponse>>() {})
        } else {
            null
        }
    }

    @Throws(JsonProcessingException::class)
    override fun saveAllPosts(postResponses: List<PostResponse>, pageRequest: PageRequest) {
        val key = this.getKeyFromGetAllPosts(pageRequest)
        val json = redisObjectMapper.writeValueAsString(postResponses)
        redisTemplate.opsForValue()[key] = json
    }

    override fun clear() {
        Objects.requireNonNull(redisTemplate.connectionFactory).connection
            .serverCommands()
    }
}
