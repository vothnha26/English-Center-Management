package com.trungtamdaotao.model.dao.impl;

import java.util.List;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.teacher.ITeacherDAO;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.util.DbManager;

import jakarta.persistence.EntityManager;

public class TeacherDAOImpl extends AbstractDAO<Teacher> implements ITeacherDAO {

    public TeacherDAOImpl() {
        super(Teacher.class);
    }

    @Override
    public List<Teacher> findByNameOrPhone(String keyword) {
        // Dùng JPQL + tham số :kw để chống SQL Injection
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT t FROM Teacher t " +
                          "WHERE LOWER(t.fullName) LIKE :kw OR t.phone LIKE :kw";
            String param = "%" + keyword.toLowerCase() + "%";
            return em.createQuery(jpql, Teacher.class)
                     .setParameter("kw", param)
                     .getResultList();
        }
    }
}