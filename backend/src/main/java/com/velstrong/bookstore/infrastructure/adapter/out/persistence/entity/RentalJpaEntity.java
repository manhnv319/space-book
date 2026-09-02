package com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Getter
@Setter
public class RentalJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderItemId;

    @Column(nullable = false)
    private Long bookCopyId;

    @Column(nullable = false)
    private Long userId;

    private String rentalTermUnit;
    private Integer rentalTermValue;
    private Long depositAmount;
    private LocalDate rentalStartDate;
    private LocalDate plannedReturnDate;
    private LocalDate actualReturnDate;
    private String status;
    private Integer lateDays;
    private Long lateFeeAmount;
    private Long damageFeeAmount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
