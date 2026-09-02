package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cartId;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private String itemType;

    private Integer quantity;
    private Integer rentalTermValue;
    private String rentalTermUnit;
}
