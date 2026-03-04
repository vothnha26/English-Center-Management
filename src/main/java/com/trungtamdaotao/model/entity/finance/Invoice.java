package com.trungtamdaotao.model.entity.finance;

import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.operations.Promotion;
import com.trungtamdaotao.model.entity.enums.InvoiceStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoice_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "issue_date")
    private LocalDate issueDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.Issued;

    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
}