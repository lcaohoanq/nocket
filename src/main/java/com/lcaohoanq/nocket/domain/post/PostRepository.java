package com.lcaohoanq.nocket.domain.post;

import com.lcaohoanq.nocket.domain.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUser(User user);
    
}
