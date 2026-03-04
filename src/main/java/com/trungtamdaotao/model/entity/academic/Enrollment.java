package com.trungtamdaotao.model.entity.academic;

import com.trungtamdaotao.model.entity.core.*;
import com.trungtamdaotao.model.entity.enums.EnrollmentStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "enrollments", uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "class_id"})})
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollment_id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity clazz;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.Enrolled;
}
