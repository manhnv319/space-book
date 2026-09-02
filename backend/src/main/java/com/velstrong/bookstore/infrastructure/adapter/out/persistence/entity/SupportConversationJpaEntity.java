package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_conversations")
@Getter
@Setter
public class SupportConversationJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime lastMessageAt;
    @Column(nullable = false)
    private int staffUnreadCount;
    @Column(nullable = false)
    private int customerUnreadCount;
    @Column(nullable = false, length = 2000)
    private String lastMessagePreview;
    private Long assignedStaffUserId;
}
