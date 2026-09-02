package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String orderCode;

    @Column(nullable = false)
    private String orderType;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String paymentStatus;

    @Column(nullable = false)
    private String paymentMethod;

    private Integer totalItems;
    private Long totalAmount;
    private Long totalDeposit;
    private Long totalDiscount;
    private Long voucherId;
    private Long shippingAddressId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Version
    private Long version;

    public Long getVersion() {
        return version;
    }
}
