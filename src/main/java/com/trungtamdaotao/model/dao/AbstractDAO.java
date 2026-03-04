package com.trungtamdaotao.model.dao;

import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public abstract class AbstractDAO<T> implements IGenericDAO<T> {
    protected Class<T> entityClass;

    public AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public List<T> findAll() {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            return em.createQuery("from " + entityClass.getName(), entityClass).getResultList();
        }
    }

    @Override
    public T findById(int id) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            return em.find(entityClass, id);
        }
    }

    @Override
    public void save(T entity) {
        executeInsideTransaction(em -> em.persist(entity));
    }

    @Override
    public void update(T entity) {
        executeInsideTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(int id) {
        executeInsideTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) em.remove(entity);
        });
    }

    // Một hàm bổ trợ dùng Lambda để quản lý Transaction cho gọn (SOLID)
    private void executeInsideTransaction(java.util.function.Consumer<EntityManager> action) {
        EntityManager em = DbManager.getFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}