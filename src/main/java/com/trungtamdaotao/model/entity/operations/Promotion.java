package com.trungtamdaotao.model.entity.operations;

import com.trungtamdaotao.model.entity.enums.DiscountType;
import com.trungtamdaotao.model.entity.enums.Status;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotion_id;

    @Column(name = "promo_name", nullable = false)
    private String promoName;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType; // Cần tạo Enum: Percent, Amount

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.Active;

    // Getters and Setters
}