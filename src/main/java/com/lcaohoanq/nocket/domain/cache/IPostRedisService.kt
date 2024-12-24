package com.lcaohoanq.nocket.domain.cache

import com.fasterxml.jackson.core.JsonProcessingException
import com.lcaohoanq.nocket.domain.post.PostPort.PostResponse
import org.springframework.data.domain.PageRequest

interface IPostRedisService {
    fun clear() //Clear cached data in Redis

    @Throws(JsonProcessingException::class)
    fun getAllPosts(pageRequest: PageRequest): List<PostResponse>?

    @Throws(JsonProcessingException::class)
    fun saveAllPosts(postResponses: List<PostResponse>, pageRequest: PageRequest)
}
