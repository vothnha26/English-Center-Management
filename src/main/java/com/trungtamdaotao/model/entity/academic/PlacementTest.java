package com.trungtamdaotao.model.entity.academic;

import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.CourseLevel;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "placement_tests")
public class PlacementTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long test_id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "test_date")
    private LocalDate testDate;

    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(name = "suggest_level")
    private CourseLevel suggestedLevel; // Dùng lại Enum CourseLevel đã tạo

    private String note;

    // Getters and Setters
}