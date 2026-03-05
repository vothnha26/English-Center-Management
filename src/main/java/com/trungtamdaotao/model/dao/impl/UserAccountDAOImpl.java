package com.trungtamdaotao.model.dao.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.system.IUserAccountDAO;
import com.trungtamdaotao.model.entity.system.UserAccount;

public class UserAccountDAOImpl extends AbstractDAO<UserAccount> implements IUserAccountDAO {
    public UserAccountDAOImpl() {
        super(UserAccount.class);
    }
}