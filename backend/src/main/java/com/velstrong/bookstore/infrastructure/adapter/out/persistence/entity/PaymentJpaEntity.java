package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class PaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;
    private Long customerSubscriptionId;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String status;

    private String transactionId;
    private String gatewayRef;
    @Column(unique = true)
    private String transferReference;
    private LocalDateTime expiresAt;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    @Version
    private Long version;

    public Long getVersion() {
        return version;
    }
}
