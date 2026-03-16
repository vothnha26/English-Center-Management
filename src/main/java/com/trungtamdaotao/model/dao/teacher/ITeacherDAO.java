package com.trungtamdaotao.model.dao.teacher;

import java.util.List;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.core.Teacher;

public interface ITeacherDAO extends IGenericDAO<Teacher> {
    /** Tìm giáo viên theo tên hoặc số điện thoại (hỗ trợ ô tìm kiếm) */
    List<Teacher> findByNameOrPhone(String keyword);
}