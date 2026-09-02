package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
public class VoucherJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;
    private String description;
    private String discountType;
    private Long discountValue;
    private Long maxDiscountAmount;
    private Long minOrderAmount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer usageLimitTotal;
    private Integer usageLimitPerUser;
    private Integer usedCount;
    private Byte status;

    @Version
    private Long version;

    public Long getVersion() {
        return version;
    }
}
