package com.trungtamdaotao.model.entity.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.PaymentMethod;
import com.trungtamdaotao.model.entity.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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

    public Payment() {}

    public Long getPaymentId()                 { return payment_id; }
    public Student getStudent()                { return student; }
    public void setStudent(Student s)          { this.student = s; }
    public Invoice getInvoice()                { return invoice; }
    public void setInvoice(Invoice i)          { this.invoice = i; }
    public BigDecimal getAmount()              { return amount; }
    public void setAmount(BigDecimal a)        { this.amount = a; }
    public LocalDateTime getPaymentDate()      { return paymentDate; }
    public void setPaymentDate(LocalDateTime d){ this.paymentDate = d; }
    public PaymentMethod getPaymentMethod()    { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod m) { this.paymentMethod = m; }
    public PaymentStatus getStatus()           { return status; }
    public void setStatus(PaymentStatus s)     { this.status = s; }
    public String getReferenceCode()           { return referenceCode; }
    public void setReferenceCode(String r)     { this.referenceCode = r; }
}