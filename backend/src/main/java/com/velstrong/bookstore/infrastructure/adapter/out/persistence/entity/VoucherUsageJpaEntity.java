package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher_usages")
@Getter
@Setter
public class VoucherUsageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long voucherId;

    private Long userId;
    private Long orderId;
    private Long discountAmount;
    private String status;
    private LocalDateTime reservedAt;
    private LocalDateTime committedAt;
    private LocalDateTime expiredAt;
}
