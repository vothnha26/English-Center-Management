package com.trungtamdaotao.controller.academic;

import com.trungtamdaotao.model.dao.academic.ICourseDAO;
import com.trungtamdaotao.model.dao.impl.CourseDAOImpl;
import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.model.service.academic.CourseService;

import java.util.List;

public class CourseController {
    private final CourseService courseService;

    public CourseController() {
        ICourseDAO courseDAO = new CourseDAOImpl();
        this.courseService = new CourseService(courseDAO);
    }

    /**
     * Tạo khóa học mới
     * @return message thông báo
     */
    public String createCourse(Course course) {
        try {
            courseService.createCourse(course);
            return "Tạo khóa học thành công!";
        } catch (IllegalArgumentException e) {
            return "Lỗi: " + e.getMessage();
        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    /**
     * Cập nhật khóa học
     * @return message thông báo
     */
    public String updateCourse(Course course) {
        try {
            courseService.updateCourse(course);
            return "Cập nhật khóa học thành công!";
        } catch (IllegalArgumentException e) {
            return "Lỗi: " + e.getMessage();
        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    /**
     * Xóa khóa học
     * @return message thông báo
     */
    public String deleteCourse(int id) {
        try {
            courseService.deleteCourse(id);
            return "Xóa khóa học thành công!";
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }

    /**
     * Lấy tất cả khóa học
     */
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    /**
     * Lấy khóa học theo ID
     */
    public Course getCourseById(int id) {
        return courseService.getCourseById(id);
    }

    /**
     * Tìm kiếm khóa học
     */
    public List<Course> searchCourses(String keyword) {
        return courseService.searchCourses(keyword);
    }

    /**
     * Lấy khóa học đang hoạt động
     */
    public List<Course> getActiveCourses() {
        return courseService.getActiveCourses();
    }

    /**
     * Lọc khóa học theo status
     */
    public List<Course> getCoursesByStatus(Status status) {
        return courseService.getCoursesByStatus(status);
    }
}
