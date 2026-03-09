package com.trungtamdaotao.model.dao.impl;

import java.util.List;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.student.IStudentDAO;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.util.DbManager;

import jakarta.persistence.EntityManager;

public class StudentDAOImpl extends AbstractDAO<Student> implements IStudentDAO {
    public StudentDAOImpl() {
        super(Student.class);
    }

    @Override
    public List<Student> findByNameOrPhone(String keyword) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT s FROM Student s WHERE LOWER(s.fullName) LIKE :kw OR s.phone LIKE :kw";
            String param = "%" + keyword.toLowerCase() + "%";
            return em.createQuery(jpql, Student.class)
                     .setParameter("kw", param)
                     .getResultList();
        }
    }

    @Override
    public List<Student> searchByName(String keyword) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT s FROM Student s WHERE LOWER(s.fullName) LIKE LOWER(:keyword) ORDER BY s.fullName ASC";
            return em.createQuery(jpql, Student.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .getResultList();
        }
    }

    @Override
    public Student findByEmail(String email) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT s FROM Student s WHERE s.email = :email";
            List<Student> results = em.createQuery(jpql, Student.class)
                    .setParameter("email", email)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }

    @Override
    public Student findByPhone(String phone) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT s FROM Student s WHERE s.phone = :phone";
            List<Student> results = em.createQuery(jpql, Student.class)
                    .setParameter("phone", phone)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }

    @Override
    public Student findByIdWithRelations(int id) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT s FROM Student s WHERE s.student_id = :id";
            List<Student> results = em.createQuery(jpql, Student.class)
                    .setParameter("id", (long) id)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }
}
