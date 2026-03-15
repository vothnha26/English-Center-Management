package com.trungtamdaotao.model.dao.academic;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Course;

import java.util.List;

public interface IClassDAO extends IGenericDAO<ClassEntity> {
    /**
     * Tìm kiếm lớp học theo tên (LIKE)
     */
    List<ClassEntity> searchByName(String keyword);

    /**
     * Tìm lớp học theo tên chính xác
     */
    ClassEntity findByClassName(String className);

    /**
     * Lọc lớp học theo khóa học
     */
    List<ClassEntity> findByCourse(Course course);

    /**
     * Lọc lớp học theo courseId
     */
    List<ClassEntity> findByCourseId(Long courseId);

    /**
     * Lấy lớp học theo ID với eager loading các entity liên quan
     */
    ClassEntity findByIdWithRelations(int id);
}
