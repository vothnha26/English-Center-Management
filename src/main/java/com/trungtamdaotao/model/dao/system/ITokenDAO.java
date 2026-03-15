package com.trungtamdaotao.model.dao.system;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.enums.TokenType;
import com.trungtamdaotao.model.entity.system.Token;
import com.trungtamdaotao.model.entity.system.UserAccount;

public interface ITokenDAO extends IGenericDAO<Token> {
    Token findByTokenValue(String tokenValue);
    void markAsUsed(String tokenValue);
    Token findValidTokenByUserAndType(UserAccount userAccount, TokenType type);
}