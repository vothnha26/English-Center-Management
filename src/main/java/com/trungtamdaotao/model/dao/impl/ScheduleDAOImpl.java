package com.trungtamdaotao.model.dao.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.academic.IScheduleDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Schedule;
import com.trungtamdaotao.model.entity.operations.Room;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class ScheduleDAOImpl extends AbstractDAO<Schedule> implements IScheduleDAO {
    public ScheduleDAOImpl() {
        super(Schedule.class);
    }

    @Override
    public List<Schedule> findAll() {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT s FROM Schedule s " +
                         "LEFT JOIN FETCH s.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH s.room " +
                         "ORDER BY s.studyDate DESC, s.startTime ASC";
            return em.createQuery(jpql, Schedule.class).getResultList();
        }
    }

    @Override
    public List<Schedule> findByClass(ClassEntity clazz) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT s FROM Schedule s " +
                         "LEFT JOIN FETCH s.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH s.room " +
                         "WHERE s.clazz = :clazz " +
                         "ORDER BY s.studyDate ASC, s.startTime ASC";
            return em.createQuery(jpql, Schedule.class)
                    .setParameter("clazz", clazz)
                    .getResultList();
        }
    }

    @Override
    public List<Schedule> findByClassId(Long classId) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT s FROM Schedule s " +
                         "LEFT JOIN FETCH s.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH s.room " +
                         "WHERE s.clazz.class_id = :classId " +
                         "ORDER BY s.studyDate ASC, s.startTime ASC";
            return em.createQuery(jpql, Schedule.class)
                    .setParameter("classId", classId)
                    .getResultList();
        }
    }

    @Override
    public List<Schedule> findByDate(LocalDate date) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT s FROM Schedule s " +
                         "LEFT JOIN FETCH s.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH s.room " +
                         "WHERE s.studyDate = :date " +
                         "ORDER BY s.startTime ASC";
            return em.createQuery(jpql, Schedule.class)
                    .setParameter("date", date)
                    .getResultList();
        }
    }

    @Override
    public List<Schedule> findByRoom(Room room) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT s FROM Schedule s " +
                         "LEFT JOIN FETCH s.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH s.room " +
                         "WHERE s.room = :room " +
                         "ORDER BY s.studyDate DESC, s.startTime ASC";
            return em.createQuery(jpql, Schedule.class)
                    .setParameter("room", room)
                    .getResultList();
        }
    }

    @Override
    public List<Schedule> findByRoomAndDate(Room room, LocalDate date) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT s FROM Schedule s " +
                         "LEFT JOIN FETCH s.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH s.room " +
                         "WHERE s.room = :room AND s.studyDate = :date " +
                         "ORDER BY s.startTime ASC";
            return em.createQuery(jpql, Schedule.class)
                    .setParameter("room", room)
                    .setParameter("date", date)
                    .getResultList();
        }
    }

    @Override
    public List<Schedule> findByDateRange(LocalDate startDate, LocalDate endDate) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT DISTINCT s FROM Schedule s " +
                         "LEFT JOIN FETCH s.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH s.room " +
                         "WHERE s.studyDate BETWEEN :startDate AND :endDate " +
                         "ORDER BY s.studyDate ASC, s.startTime ASC";
            return em.createQuery(jpql, Schedule.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        }
    }

    @Override
    public Schedule findByIdWithRelations(int id) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT s FROM Schedule s " +
                         "LEFT JOIN FETCH s.clazz c " +
                         "LEFT JOIN FETCH c.course " +
                         "LEFT JOIN FETCH s.room " +
                         "WHERE s.schedule_id = :id";
            List<Schedule> results = em.createQuery(jpql, Schedule.class)
                    .setParameter("id", Long.valueOf(id))
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }
}
