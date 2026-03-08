package com.trungtamdaotao.model.entity.academic;

import java.time.LocalDate;

import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.EnrollmentStatus;

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
import jakarta.persistence.UniqueConstraint;

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

    public Enrollment() {}

    public Long getEnrollmentId()              { return enrollment_id; }
    public Student getStudent()                { return student; }
    public void setStudent(Student s)          { this.student = s; }
    public ClassEntity getClazz()              { return clazz; }
    public void setClazz(ClassEntity c)        { this.clazz = c; }
    public LocalDate getEnrollmentDate()       { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate d) { this.enrollmentDate = d; }
    public EnrollmentStatus getStatus()        { return status; }
    public void setStatus(EnrollmentStatus s)  { this.status = s; }
}
