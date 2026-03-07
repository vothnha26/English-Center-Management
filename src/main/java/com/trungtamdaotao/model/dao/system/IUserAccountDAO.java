package com.trungtamdaotao.model.dao.system;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.system.UserAccount;

public interface IUserAccountDAO extends IGenericDAO<UserAccount> {

    UserAccount findByUsername(String username);
}