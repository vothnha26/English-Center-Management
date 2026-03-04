package com.trungtamdaotao.model.entity.academic;

import com.trungtamdaotao.model.entity.core.*;
import com.trungtamdaotao.model.entity.enums.AttendanceStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "attendances")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendance_id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity clazz;

    private LocalDate attend_date;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status = AttendanceStatus.Present;
}