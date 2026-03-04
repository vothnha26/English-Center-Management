package com.trungtamdaotao.model.entity.core;

import com.trungtamdaotao.model.entity.enums.CourseLevel;
import com.trungtamdaotao.model.entity.enums.DurationUnit;
import com.trungtamdaotao.model.entity.enums.Status;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long course_id;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private CourseLevel level; // Beginner, Intermediate, Advanced

    private Integer duration;

    @Column(name = "duration_unit")
    @Enumerated(EnumType.STRING)
    private DurationUnit durationUnit = DurationUnit.Week;

    @Column(nullable = false)
    private BigDecimal fee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private Status status = Status.Active;

    // Getters and Setters
}