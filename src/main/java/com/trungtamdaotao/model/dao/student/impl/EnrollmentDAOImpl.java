package com.trungtamdaotao.model.dao.student.impl;

import java.util.List;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.student.IEnrollmentDAO;
import com.trungtamdaotao.model.entity.academic.Enrollment;
import com.trungtamdaotao.util.DbManager;

import jakarta.persistence.EntityManager;

public class EnrollmentDAOImpl extends AbstractDAO<Enrollment> implements IEnrollmentDAO {

    public EnrollmentDAOImpl() {
        super(Enrollment.class);
    }

    @Override
    public List<Enrollment> findAll() {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT e FROM Enrollment e " +
                          "JOIN FETCH e.student s " +
                          "JOIN FETCH e.clazz c";
            return em.createQuery(jpql, Enrollment.class).getResultList();
        }
    }

    @Override
    public List<Enrollment> findByStudentId(Long studentId) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT e FROM Enrollment e " +
                          "JOIN FETCH e.student s " +
                          "JOIN FETCH e.clazz c " +
                          "WHERE s.student_id = :sid";
            return em.createQuery(jpql, Enrollment.class)
                     .setParameter("sid", studentId)
                     .getResultList();
        }
    }

    @Override
    public List<Enrollment> findByClassId(Long classId) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT e FROM Enrollment e " +
                          "JOIN FETCH e.student s " +
                          "JOIN FETCH e.clazz c " +
                          "WHERE c.class_id = :cid";
            return em.createQuery(jpql, Enrollment.class)
                     .setParameter("cid", classId)
                     .getResultList();
        }
    }

    @Override
    public boolean existsByStudentAndClass(Long studentId, Long classId) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT COUNT(e) FROM Enrollment e " +
                          "WHERE e.student.student_id = :sid AND e.clazz.class_id = :cid";
            Long count = em.createQuery(jpql, Long.class)
                           .setParameter("sid", studentId)
                           .setParameter("cid", classId)
                           .getSingleResult();
            return count > 0;
        }
    }
}
