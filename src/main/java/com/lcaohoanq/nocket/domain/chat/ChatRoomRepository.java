package com.lcaohoanq.nocket.domain.chat;

import com.lcaohoanq.nocket.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByUser1AndUser2(User user1, User user2);

}
