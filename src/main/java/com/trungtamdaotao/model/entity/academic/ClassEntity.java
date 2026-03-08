package com.trungtamdaotao.model.entity.academic;

import java.time.LocalDate;

import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.ClassStatus;
import com.trungtamdaotao.model.entity.operations.Room;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
    private ClassStatus status = ClassStatus.Planned;

    public ClassEntity() {}

    public Long getClassId()              { return class_id; }
    public String getClassName()          { return className; }
    public void setClassName(String n)    { this.className = n; }
    public Course getCourse()             { return course; }
    public void setCourse(Course c)       { this.course = c; }
    public Teacher getTeacher()           { return teacher; }
    public void setTeacher(Teacher t)     { this.teacher = t; }
    public Room getRoom()                 { return room; }
    public void setRoom(Room r)           { this.room = r; }
    public LocalDate getStartDate()       { return startDate; }
    public void setStartDate(LocalDate d) { this.startDate = d; }
    public int getMaxStudent()            { return maxStudent; }
    public void setMaxStudent(int m)      { this.maxStudent = m; }
    public ClassStatus getStatus()        { return status; }
    public void setStatus(ClassStatus s)  { this.status = s; }

    @Override
    public String toString() { return className; }
}
