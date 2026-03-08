package com.trungtamdaotao.model.dao.system;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.system.ActivationToken;
import java.util.Optional;

public interface IActivationTokenDAO extends IGenericDAO<ActivationToken> {
    ActivationToken findByToken(String token);
    ActivationToken findByUserId(Long userId);
}