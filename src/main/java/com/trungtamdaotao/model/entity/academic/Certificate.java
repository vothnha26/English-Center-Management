package com.trungtamdaotao.model.entity.academic;

import com.trungtamdaotao.model.entity.core.Student;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificate_id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity clazz;

    @Column(name = "cert_name", nullable = false)
    private String certName;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "serial_no", unique = true, nullable = false)
    private String serialNo;

    // Getters and Setters
}