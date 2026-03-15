package com.trungtamdaotao.model.entity.academic;

import com.trungtamdaotao.model.entity.core.*;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long result_id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity clazz;

    private BigDecimal score;
    private String grade;
    private String comment;
}

