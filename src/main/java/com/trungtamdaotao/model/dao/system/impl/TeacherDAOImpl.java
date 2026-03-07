package com.trungtamdaotao.model.dao.system.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.system.ITeacherDAO;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TeacherDAOImpl extends AbstractDAO<Teacher> implements ITeacherDAO {
    public TeacherDAOImpl() {
        super(Teacher.class);
    }

    @Override
    public List<Teacher> findBySpecialty(String spec) {
        // 1. Lấy EntityManager từ nhà máy DbConnection
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {

            // 2. lệnh JPQL
            String jpql = "SELECT t FROM Teacher t WHERE t.specialty = :spec";

            // 3. Thực thi truy vấn và truyền tham số :spec để chống SQL Injection
            return em.createQuery(jpql, Teacher.class)
                    .setParameter("spec", spec)
                    .getResultList();
        }
    }
}
