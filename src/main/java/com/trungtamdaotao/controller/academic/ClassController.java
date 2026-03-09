package com.trungtamdaotao.controller.academic;

import com.trungtamdaotao.model.dao.academic.IClassDAO;
import com.trungtamdaotao.model.dao.impl.ClassDAOImpl;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.model.service.academic.ClassService;

import java.util.List;

public class ClassController {
    private final ClassService classService;

    public ClassController() {
        IClassDAO classDAO = new ClassDAOImpl();
        this.classService = new ClassService(classDAO);
    }

    /**
     * Tạo lớp học mới
     * @return message thông báo
     */
    public String createClass(ClassEntity classEntity) {
        try {
            classService.createClass(classEntity);
            return "Tạo lớp học thành công!";
        } catch (IllegalArgumentException e) {
            return "Lỗi: " + e.getMessage();
        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    /**
     * Cập nhật lớp học
     * @return message thông báo
     */
    public String updateClass(ClassEntity classEntity) {
        try {
            classService.updateClass(classEntity);
            return "Cập nhật lớp học thành công!";
        } catch (IllegalArgumentException e) {
            return "Lỗi: " + e.getMessage();
        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    /**
     * Xóa lớp học
     * @return message thông báo
     */
    public String deleteClass(int id) {
        try {
            classService.deleteClass(id);
            return "Xóa lớp học thành công!";
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }

    /**
     * Lấy tất cả lớp học
     */
    public List<ClassEntity> getAllClasses() {
        return classService.getAllClasses();
    }

    /**
     * Lấy lớp học theo ID
     */
    public ClassEntity getClassById(int id) {
        return classService.getClassById(id);
    }

    /**
     * Tìm kiếm lớp học
     */
    public List<ClassEntity> searchClasses(String keyword) {
        return classService.searchClasses(keyword);
    }

    /**
     * Lọc lớp học theo khóa học
     */
    public List<ClassEntity> getClassesByCourse(Course course) {
        return classService.getClassesByCourse(course);
    }

    /**
     * Lọc lớp học theo courseId
     */
    public List<ClassEntity> getClassesByCourseId(Long courseId) {
        return classService.getClassesByCourseId(courseId);
    }
}
