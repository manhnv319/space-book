package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;
    @Column(nullable = false)
    private Long bookId;
    private Long bookCopyId;
    @Column(nullable = false)
    private String itemType;
    private Integer quantity;
    private Long unitPrice;
    private Long depositAmount;
    private Integer rentalTermValue;
    private String rentalTermUnit;
    private Long subtotal;
}
