package com.trungtamdaotao.model.dao.system.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.system.ITokenDAO;
import com.trungtamdaotao.model.entity.enums.TokenType;
import com.trungtamdaotao.model.entity.system.Token;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;

public class TokenDAOImpl extends AbstractDAO<Token> implements ITokenDAO {

    public TokenDAOImpl() {
        super(Token.class);
    }

    @Override
    public Token findByTokenValue(String tokenValue) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            return em.createQuery("SELECT t FROM Token t WHERE t.token_value = :value", Token.class)
                     .setParameter("value", tokenValue)
                     .getResultStream()
                     .findFirst()
                     .orElse(null);
        }
    }

    @Override
    public void markAsUsed(String tokenValue) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            Token token = findByTokenValue(tokenValue);
            if (token != null) {
                token.setUsed(true);
                em.getTransaction().begin();
                em.merge(token);
                em.getTransaction().commit();
            }
        }
    }

    @Override
    public Token findValidTokenByUserAndType(UserAccount userAccount, TokenType type) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            return em.createQuery("SELECT t FROM Token t WHERE t.userAccount = :user AND t.type = :type AND t.used = false AND t.expiry_date > CURRENT_TIMESTAMP", Token.class)
                     .setParameter("user", userAccount)
                     .setParameter("type", type)
                     .getResultStream()
                     .findFirst()
                     .orElse(null);
        }
    }
}
