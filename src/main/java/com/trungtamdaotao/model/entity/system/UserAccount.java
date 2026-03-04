package com.trungtamdaotao.model.entity.system;

import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import jakarta.persistence.*;

@Entity
@Table(name = "user_accounts")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password_hash;

    @Enumerated(EnumType.STRING)
    private AccountRole role; // Admin, Teacher, Student, Staff

    @OneToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    private boolean is_active = true;

    // Getters, Setters
}
