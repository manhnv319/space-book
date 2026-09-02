package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notifications", indexes = @Index(name = "idx_user_notifications_user_created", columnList = "user_id,created_at"))
@Getter @Setter
public class UserNotificationJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false, length = 32) private String type;
    @Column(nullable = false, length = 180) private String title;
    @Column(nullable = false, length = 2000) private String body;
    @Column(nullable = false, length = 500) private String targetPath;
    private LocalDateTime readAt;
    @Column(nullable = false) private LocalDateTime createdAt;
}
