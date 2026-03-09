package com.trungtamdaotao.model.dao.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.academic.IAttendanceDAO;
import com.trungtamdaotao.model.entity.academic.Attendance;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.AttendanceStatus;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class AttendanceDAOImpl extends AbstractDAO<Attendance> implements IAttendanceDAO {
    public AttendanceDAOImpl() {
        super(Attendance.class);
    }

    @Override
    public List<Attendance> findAll() {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "ORDER BY a.attend_date DESC, a.attendance_id DESC";
            return em.createQuery(jpql, Attendance.class).getResultList();
        }
    }

    @Override
    public List<Attendance> findByStudent(Student student) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.student = :student " +
                         "ORDER BY a.attend_date DESC";
            return em.createQuery(jpql, Attendance.class)
                    .setParameter("student", student)
                    .getResultList();
        }
    }

    @Override
    public List<Attendance> findByStudentId(Long studentId) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.student.student_id = :studentId " +
                         "ORDER BY a.attend_date DESC";
            return em.createQuery(jpql, Attendance.class)
                    .setParameter("studentId", studentId)
                    .getResultList();
        }
    }

    @Override
    public List<Attendance> findByClass(ClassEntity clazz) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.clazz = :clazz " +
                         "ORDER BY a.attend_date DESC, a.student.fullName ASC";
            return em.createQuery(jpql, Attendance.class)
                    .setParameter("clazz", clazz)
                    .getResultList();
        }
    }

    @Override
    public List<Attendance> findByClassId(Long classId) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.clazz.class_id = :classId " +
                         "ORDER BY a.attend_date DESC, a.student.fullName ASC";
            return em.createQuery(jpql, Attendance.class)
                    .setParameter("classId", classId)
                    .getResultList();
        }
    }

    @Override
    public List<Attendance> findByDate(LocalDate date) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.attend_date = :date " +
                         "ORDER BY a.clazz.className, a.student.fullName ASC";
            return em.createQuery(jpql, Attendance.class)
                    .setParameter("date", date)
                    .getResultList();
        }
    }

    @Override
    public List<Attendance> findByClassAndDate(ClassEntity clazz, LocalDate date) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.clazz = :clazz AND a.attend_date = :date " +
                         "ORDER BY a.student.fullName ASC";
            return em.createQuery(jpql, Attendance.class)
                    .setParameter("clazz", clazz)
                    .setParameter("date", date)
                    .getResultList();
        }
    }

    @Override
    public List<Attendance> findByClassIdAndDate(Long classId, LocalDate date) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.clazz.class_id = :classId AND a.attend_date = :date " +
                         "ORDER BY a.student.fullName ASC";
            return em.createQuery(jpql, Attendance.class)
                    .setParameter("classId", classId)
                    .setParameter("date", date)
                    .getResultList();
        }
    }

    @Override
    public List<Attendance> findByStudentAndClass(Student student, ClassEntity clazz) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.student = :student AND a.clazz = :clazz " +
                         "ORDER BY a.attend_date DESC";
            return em.createQuery(jpql, Attendance.class)
                    .setParameter("student", student)
                    .setParameter("clazz", clazz)
                    .getResultList();
        }
    }

    @Override
    public Attendance findByStudentAndClassAndDate(Student student, ClassEntity clazz, LocalDate date) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.student = :student AND a.clazz = :clazz AND a.attend_date = :date";
            List<Attendance> results = em.createQuery(jpql, Attendance.class)
                    .setParameter("student", student)
                    .setParameter("clazz", clazz)
                    .setParameter("date", date)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }

    @Override
    public List<Attendance> findByStatus(AttendanceStatus status) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.status = :status " +
                         "ORDER BY a.attend_date DESC";
            return em.createQuery(jpql, Attendance.class)
                    .setParameter("status", status)
                    .getResultList();
        }
    }

    @Override
    public Attendance findByIdWithRelations(int id) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT a FROM Attendance a " +
                         "LEFT JOIN FETCH a.student " +
                         "LEFT JOIN FETCH a.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "WHERE a.attendance_id = :id";
            List<Attendance> results = em.createQuery(jpql, Attendance.class)
                    .setParameter("id", Long.valueOf(id))
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }
}
