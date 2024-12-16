package com.lcaohoanq.nocket.domain.chat;

import com.lcaohoanq.nocket.base.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_messages")
@SuperBuilder
public class ChatMessage extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "chat_room_id", referencedColumnName = "id", nullable = false)
    private ChatRoom chatRoomId;
    
    private Long senderId;
    private String content;
    private String attachmentUrl;
    private LocalDateTime timestamp;
    private boolean isRead;
    
} 