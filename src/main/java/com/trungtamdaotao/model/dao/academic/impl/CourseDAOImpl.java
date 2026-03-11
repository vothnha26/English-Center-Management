package com.trungtamdaotao.model.dao.academic.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.academic.ICourseDAO;
import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CourseDAOImpl extends AbstractDAO<Course> implements ICourseDAO {
    public CourseDAOImpl() {
        super(Course.class);
    }

    @Override
    public List<Course> searchByName(String keyword) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT c FROM Course c WHERE LOWER(c.courseName) LIKE LOWER(:keyword)";
            return em.createQuery(jpql, Course.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .getResultList();
        }
    }

    @Override
    public Course findByCourseName(String courseName) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT c FROM Course c WHERE c.courseName = :name";
            List<Course> results = em.createQuery(jpql, Course.class)
                    .setParameter("name", courseName)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }

    @Override
    public List<Course> findActiveCourses() {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT c FROM Course c WHERE c.status = :status";
            return em.createQuery(jpql, Course.class)
                    .setParameter("status", Status.Active)
                    .getResultList();
        }
    }
}
