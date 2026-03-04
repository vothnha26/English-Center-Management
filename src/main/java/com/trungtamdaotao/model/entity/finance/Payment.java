package com.trungtamdaotao.model.entity.finance;

import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.PaymentMethod;
import com.trungtamdaotao.model.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payment_id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod = PaymentMethod.Cash;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.Completed;

    @Column(name = "reference_code")
    private String referenceCode;

    // Getters and Setters
}