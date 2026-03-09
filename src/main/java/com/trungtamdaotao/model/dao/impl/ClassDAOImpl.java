package com.trungtamdaotao.model.dao.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.academic.IClassDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ClassDAOImpl extends AbstractDAO<ClassEntity> implements IClassDAO {
    public ClassDAOImpl() {
        super(ClassEntity.class);
    }

    @Override
    public List<ClassEntity> findAll() {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT c FROM ClassEntity c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH c.teacher " +
                         "LEFT JOIN FETCH c.room";
            return em.createQuery(jpql, ClassEntity.class).getResultList();
        }
    }

    @Override
    public List<ClassEntity> searchByName(String keyword) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT c FROM ClassEntity c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH c.teacher " +
                         "LEFT JOIN FETCH c.room " +
                         "WHERE LOWER(c.className) LIKE LOWER(:keyword)";
            return em.createQuery(jpql, ClassEntity.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .getResultList();
        }
    }

    @Override
    public ClassEntity findByClassName(String className) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT c FROM ClassEntity c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH c.teacher " +
                         "LEFT JOIN FETCH c.room " +
                         "WHERE c.className = :name";
            List<ClassEntity> results = em.createQuery(jpql, ClassEntity.class)
                    .setParameter("name", className)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }

    @Override
    public List<ClassEntity> findByCourse(Course course) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT c FROM ClassEntity c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH c.teacher " +
                         "LEFT JOIN FETCH c.room " +
                         "WHERE c.course = :course";
            return em.createQuery(jpql, ClassEntity.class)
                    .setParameter("course", course)
                    .getResultList();
        }
    }

    @Override
    public List<ClassEntity> findByCourseId(Long courseId) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT c FROM ClassEntity c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH c.teacher " +
                         "LEFT JOIN FETCH c.room " +
                         "WHERE c.course.course_id = :courseId";
            return em.createQuery(jpql, ClassEntity.class)
                    .setParameter("courseId", courseId)
                    .getResultList();
        }
    }

    @Override
    public ClassEntity findByIdWithRelations(int id) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT c FROM ClassEntity c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH c.teacher " +
                         "LEFT JOIN FETCH c.room " +
                         "WHERE c.class_id = :id";
            List<ClassEntity> results = em.createQuery(jpql, ClassEntity.class)
                    .setParameter("id", Long.valueOf(id))
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }
}
