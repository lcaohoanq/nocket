package com.lcaohoanq.nocket.domain.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcaohoanq.nocket.domain.post.Post;
import com.lcaohoanq.nocket.domain.post.PostPort.PostResponse;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostRedisService implements IPostRedisService{

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;
    @Value("${spring.data.redis.use-redis-cache}")
    private boolean useRedisCache;

    private String getKeyFromGetAllPosts(PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();
        String sortDirection = Objects.requireNonNull(sort.getOrderFor("id"))
            .getDirection() == Sort.Direction.ASC ? "asc": "desc";
        return String.format("all_posts:%d:%d:%s", pageNumber, pageSize, sortDirection);
    }

    @Override
    public List<PostResponse> getAllPosts(PageRequest pageRequest) throws JsonProcessingException {

        if(!useRedisCache) {
            return null;
        }
        String key = this.getKeyFromGetAllPosts(pageRequest);
        String json = (String) redisTemplate.opsForValue().get(key);
        return json != null ?
            redisObjectMapper.readValue(json, new TypeReference<>() {
            })
            : null;
    }

    @Override
    public void saveAllPosts(List<PostResponse> postResponses, PageRequest pageRequest)
        throws JsonProcessingException {
        String key = this.getKeyFromGetAllPosts(pageRequest);
        String json = redisObjectMapper.writeValueAsString(postResponses);
        redisTemplate.opsForValue().set(key, json);
    }

    @Override
    public void clear(){
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()
            .serverCommands();
    }
    
}
