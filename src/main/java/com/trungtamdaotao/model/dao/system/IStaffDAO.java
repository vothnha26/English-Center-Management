package com.trungtamdaotao.model.dao.system;

import java.util.List;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.system.Staff;

public interface IStaffDAO extends IGenericDAO<Staff> {
    /** Tìm staff theo tên hoặc số điện thoại */
    List<Staff> findByNameOrPhone(String keyword);
}