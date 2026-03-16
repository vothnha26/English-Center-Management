package com.trungtamdaotao.model.entity.system;

import jakarta.persistence.*;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.dao.StaffRoleConverter;

@Entity
@Table(name = "staffs")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staff_id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Convert(converter = StaffRoleConverter.class)
    @Column(name = "role")
    private StaffRole role;

    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    public Staff() {}

    // Getters and Setters
    public Long getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(Long staff_id) {
        this.staff_id = staff_id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public StaffRole getRole() {
        return role;
    }

    public void setRole(StaffRole role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
