package com.trungtamdaotao.model.dao.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.system.IUserAccountDAO;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class UserAccountDAOImpl extends AbstractDAO<UserAccount> implements IUserAccountDAO {
    public UserAccountDAOImpl() {
        super(UserAccount.class);
    }

    @Override
    public UserAccount findByUsername(String username) {
        EntityManager em = DbManager.getFactory().createEntityManager();
        try {
            String jpql = "SELECT u FROM UserAccount u WHERE u.username = :user";
            return em.createQuery(jpql, UserAccount.class)
                    .setParameter("user", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Trả về null nếu không tìm thấy username
        } finally {
            em.close();
        }
    }
}