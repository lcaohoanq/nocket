package com.lcaohoanq.nocket.domain.post;

import com.lcaohoanq.nocket.domain.user.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByUser(User user);

    List<Post> findByUserIn(List<User> users);
    
}
