package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "push_subscriptions", uniqueConstraints = @UniqueConstraint(name = "uk_push_subscriptions_user_endpoint", columnNames = {"user_id", "endpoint"}))
@Getter @Setter
public class PushSubscriptionJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false, length = 2000) private String endpoint;
    @Column(nullable = false, length = 200) private String p256dh;
    @Column(nullable = false, length = 200) private String auth;
    @Column(nullable = false) private LocalDateTime createdAt;
}
