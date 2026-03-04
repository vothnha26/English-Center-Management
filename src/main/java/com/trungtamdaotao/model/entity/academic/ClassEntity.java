package com.trungtamdaotao.model.entity.academic;

import com.trungtamdaotao.model.entity.core.*;
import com.trungtamdaotao.model.entity.enums.ClassStatus;
import com.trungtamdaotao.model.entity.operations.*;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "classes")
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long class_id;

    @Column(name = "class_name", nullable = false)
    private String className;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "max_student")
    private int maxStudent;

    @Enumerated(EnumType.STRING)
    private ClassStatus status = ClassStatus.Planned; // Cần tạo Enum ClassStatus
}
