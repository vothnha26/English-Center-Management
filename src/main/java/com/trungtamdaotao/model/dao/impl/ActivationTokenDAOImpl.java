package com.trungtamdaotao.model.dao.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.system.IActivationTokenDAO;
import com.trungtamdaotao.model.entity.system.ActivationToken;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class ActivationTokenDAOImpl extends AbstractDAO<ActivationToken> implements IActivationTokenDAO {

    public ActivationTokenDAOImpl() {
        super(ActivationToken.class);
    }

    @Override
    public ActivationToken findByToken(String token) {
        EntityManager em = DbManager.getFactory().createEntityManager();
        try {
            TypedQuery<ActivationToken> query = em.createQuery(
                "SELECT t FROM ActivationToken t WHERE t.token = :token", ActivationToken.class);
            query.setParameter("token", token);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public ActivationToken findByUserId(Long userId) {
        EntityManager em = DbManager.getFactory().createEntityManager();
        try {
            TypedQuery<ActivationToken> query = em.createQuery(
                "SELECT t FROM ActivationToken t WHERE t.user.user_id = :userId", ActivationToken.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}