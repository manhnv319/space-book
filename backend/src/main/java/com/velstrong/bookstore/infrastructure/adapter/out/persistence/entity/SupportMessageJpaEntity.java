package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_messages")
@Getter
@Setter
public class SupportMessageJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long conversationId;
    @Column(nullable = false)
    private String sender;
    private Long senderUserId;
    @Column(nullable = false, length = 2000)
    private String body;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
