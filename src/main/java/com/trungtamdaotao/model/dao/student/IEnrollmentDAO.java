package com.trungtamdaotao.model.dao.student;

import java.util.List;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.academic.Enrollment;

public interface IEnrollmentDAO extends IGenericDAO<Enrollment> {
    /** Tất cả ghi danh của một học viên */
    List<Enrollment> findByStudentId(Long studentId);
    /** Tất cả ghi danh trong một lớp */
    List<Enrollment> findByClassId(Long classId);
    /** Kiểm tra học viên đã đăng ký lớp này chưa (tránh trùng) */
    boolean existsByStudentAndClass(Long studentId, Long classId);
}
