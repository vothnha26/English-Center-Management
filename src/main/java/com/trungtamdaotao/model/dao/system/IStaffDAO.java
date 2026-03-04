package com.trungtamdaotao.model.dao.system;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.system.Staff;

import java.util.List;

public interface IStaffDAO extends IGenericDAO<Staff> {
    List<Staff> findByRole(String role);
}
