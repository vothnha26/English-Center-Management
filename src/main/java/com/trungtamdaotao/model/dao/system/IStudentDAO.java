package com.trungtamdaotao.model.dao.system;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.core.Student;

import java.util.List;

public interface IStudentDAO extends IGenericDAO<Student> {
    /**
     * Tìm kiếm học viên theo tên (LIKE)
     */
    List<Student> searchByName(String keyword);

    /**
     * Tìm học viên theo email
     */
    Student findByEmail(String email);

    /**
     * Tìm học viên theo số điện thoại
     */
    Student findByPhone(String phone);

    /**
     * Lấy học viên theo ID với eager loading
     */
    Student findByIdWithRelations(int id);
}
