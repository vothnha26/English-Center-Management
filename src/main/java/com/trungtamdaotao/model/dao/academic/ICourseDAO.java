package com.trungtamdaotao.model.dao.academic;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.core.Course;

import java.util.List;

public interface ICourseDAO extends IGenericDAO<Course> {
    /**
     * Tìm kiếm khóa học theo tên (LIKE)
     */
    List<Course> searchByName(String keyword);

    /**
     * Tìm khóa học theo tên chính xác
     */
    Course findByCourseName(String courseName);

    /**
     * Lấy danh sách khóa học đang hoạt động
     */
    List<Course> findActiveCourses();
}
