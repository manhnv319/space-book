package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_bank_messages")
@Getter
@Setter
public class ProcessedBankMessageJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String messageId;
    private String bankTxnRef;
    @Column(nullable = false)
    private LocalDateTime processedAt;
}
