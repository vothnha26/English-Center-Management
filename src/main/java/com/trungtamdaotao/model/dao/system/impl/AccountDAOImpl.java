package com.trungtamdaotao.model.dao.system.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.system.IAccountDAO;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;

public class AccountDAOImpl extends AbstractDAO<UserAccount> implements IAccountDAO {

    public AccountDAOImpl() {
        super(UserAccount.class);
    }

    @Override
    public UserAccount findByUsername(String username) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            return em.createQuery("SELECT u FROM UserAccount u WHERE u.username = :username", UserAccount.class)
                     .setParameter("username", username)
                     .getResultStream()
                     .findFirst()
                     .orElse(null);
        }
    }
}
