package com.trungtamdaotao.model.entity.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.InvoiceStatus;
import com.trungtamdaotao.model.entity.operations.Promotion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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

    public Invoice() {}

    public Long getInvoiceId()               { return invoice_id; }
    public Student getStudent()              { return student; }
    public void setStudent(Student s)        { this.student = s; }
    public Promotion getPromotion()          { return promotion; }
    public void setPromotion(Promotion p)    { this.promotion = p; }
    public BigDecimal getTotalAmount()       { return totalAmount; }
    public void setTotalAmount(BigDecimal a) { this.totalAmount = a; }
    public LocalDate getIssueDate()          { return issueDate; }
    public void setIssueDate(LocalDate d)    { this.issueDate = d; }
    public InvoiceStatus getStatus()         { return status; }
    public void setStatus(InvoiceStatus s)   { this.status = s; }
    public String getNote()                  { return note; }
    public void setNote(String n)            { this.note = n; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
}