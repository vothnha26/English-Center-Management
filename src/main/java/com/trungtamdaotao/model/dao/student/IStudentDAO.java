package com.trungtamdaotao.model.dao.student;

import java.util.List;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.core.Student;

public interface IStudentDAO extends IGenericDAO<Student> {
    /** Tìm học viên theo tên hoặc số điện thoại (hỗ trợ ô tìm kiếm) */
    List<Student> findByNameOrPhone(String keyword);
}
