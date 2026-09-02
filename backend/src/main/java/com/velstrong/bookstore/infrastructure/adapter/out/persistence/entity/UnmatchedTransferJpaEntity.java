package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "unmatched_transfers")
@Getter
@Setter
public class UnmatchedTransferJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String messageId;
    private String bankTxnRef;
    private String paymentReference;
    private Long amount;
    private LocalDateTime receivedAt;
    @Column(nullable = false)
    private String reason;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
