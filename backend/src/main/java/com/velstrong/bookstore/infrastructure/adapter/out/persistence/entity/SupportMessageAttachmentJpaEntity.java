package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_message_attachments")
@Getter
@Setter
public class SupportMessageAttachmentJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long messageId;
    @Column(nullable = false, length = 500)
    private String imageUrl;
    @Column(nullable = false, length = 255)
    private String originalName;
    @Column(nullable = false, length = 100)
    private String contentType;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
