package com.trungtamdaotao.model.entity.core;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.trungtamdaotao.model.entity.enums.Gender;
import com.trungtamdaotao.model.entity.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long student_id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    private String address;

    @Column(name = "registration_date")
    private LocalDate registrationDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.Active;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Student() {}

    public Long getStudentId()           { return student_id; }
    public String getFullName()          { return fullName; }
    public void setFullName(String n)    { this.fullName = n; }
    public LocalDate getDateOfBirth()    { return dateOfBirth; }
    public void setDateOfBirth(LocalDate d) { this.dateOfBirth = d; }
    public Gender getGender()            { return gender; }
    public void setGender(Gender g)      { this.gender = g; }
    public String getPhone()             { return phone; }
    public void setPhone(String p)       { this.phone = p; }
    public String getEmail()             { return email; }
    public void setEmail(String e)       { this.email = e; }
    public String getAddress()           { return address; }
    public void setAddress(String a)     { this.address = a; }
    public LocalDate getRegistrationDate()         { return registrationDate; }
    public void setRegistrationDate(LocalDate r)   { this.registrationDate = r; }
    public Status getStatus()            { return status; }
    public void setStatus(Status s)      { this.status = s; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    @Override
    public String toString() { return fullName + " (" + phone + ")"; }
}