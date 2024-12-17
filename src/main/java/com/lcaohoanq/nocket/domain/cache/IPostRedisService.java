package com.lcaohoanq.nocket.domain.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lcaohoanq.nocket.domain.post.PostPort.PostResponse;
import java.util.List;
import org.springframework.data.domain.PageRequest;

public interface IPostRedisService {
    
    void clear(); //Clear cached data in Redis
    List<PostResponse> getAllPosts(PageRequest pageRequest) throws JsonProcessingException;
    void saveAllPosts(List<PostResponse> postResponses, PageRequest pageRequest) throws JsonProcessingException;
}
