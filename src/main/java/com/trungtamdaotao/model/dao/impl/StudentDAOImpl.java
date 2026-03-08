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
        // Dùng JPQL + tham số :kw để chống SQL Injection
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT s FROM Student s " +
                          "WHERE LOWER(s.fullName) LIKE :kw OR s.phone LIKE :kw";
            String param = "%" + keyword.toLowerCase() + "%";
            return em.createQuery(jpql, Student.class)
                     .setParameter("kw", param)
                     .getResultList();
        }
    }
}
