package com.trungtamdaotao.model.dao.student;

import java.util.List;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.core.Student;

public interface IStudentDAO extends IGenericDAO<Student> {
    /** Tìm học viên theo tên hoặc số điện thoại (hỗ trợ ô tìm kiếm) */
    List<Student> findByNameOrPhone(String keyword);

    /** Tìm kiếm học viên theo tên (LIKE) */
    List<Student> searchByName(String keyword);

    /** Tìm học viên theo email */
    Student findByEmail(String email);

    /** Tìm học viên theo số điện thoại */
    Student findByPhone(String phone);

    /** Lấy học viên theo ID với eager loading */
    Student findByIdWithRelations(int id);
}
