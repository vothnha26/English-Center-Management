package com.trungtamdaotao.model.service.academic;

import com.trungtamdaotao.model.dao.academic.ICourseDAO;
import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.model.entity.enums.Status;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CourseService {
    private final ICourseDAO courseDAO;

    public CourseService(ICourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    /**
     * Tạo mới khóa học với validation
     */
    public void createCourse(Course course) {
        // Validation 1: courseName không được rỗng
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khóa học không được để trống!");
        }

        // Validation 2: Kiểm tra trùng tên khóa học
        Course existing = courseDAO.findByCourseName(course.getCourseName());
        if (existing != null) {
            throw new IllegalArgumentException("Khóa học '" + course.getCourseName() + "' đã tồn tại!");
        }

        // Validation 3: Fee phải >= 0
        if (course.getFee() != null && course.getFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Học phí không được âm!");
        }

        // Validation 4: Duration phải > 0
        if (course.getDuration() != null && course.getDuration() <= 0) {
            throw new IllegalArgumentException("Thời lượng khóa học phải lớn hơn 0!");
        }

        courseDAO.save(course);
    }

    /**
     * Cập nhật khóa học
     */
    public void updateCourse(Course course) {
        // Validation tương tự create
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khóa học không được để trống!");
        }

        if (course.getFee() != null && course.getFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Học phí không được âm!");
        }

        if (course.getDuration() != null && course.getDuration() <= 0) {
            throw new IllegalArgumentException("Thời lượng khóa học phải lớn hơn 0!");
        }

        // Kiểm tra trùng tên (ngoại trừ chính nó)
        Course existing = courseDAO.findByCourseName(course.getCourseName());
        if (existing != null && !existing.getCourse_id().equals(course.getCourse_id())) {
            throw new IllegalArgumentException("Khóa học '" + course.getCourseName() + "' đã tồn tại!");
        }

        courseDAO.update(course);
    }

    /**
     * Xóa khóa học
     */
    public void deleteCourse(int id) {
        courseDAO.delete(id);
    }

    /**
     * Lấy tất cả khóa học
     */
    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    /**
     * Lấy khóa học theo ID
     */
    public Course getCourseById(int id) {
        return courseDAO.findById(id);
    }

    /**
     * Lambda 1: Tìm kiếm khóa học theo tên
     */
    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return courseDAO.findAll();
        }
        String lowerKey = keyword.toLowerCase();
        return courseDAO.findAll().stream()
                .filter(c -> c.getCourseName().toLowerCase().contains(lowerKey))
                .collect(Collectors.toList());
    }

    /**
     * Lambda 2: Lấy danh sách khóa học đang hoạt động
     */
    public List<Course> getActiveCourses() {
        return courseDAO.findAll().stream()
                .filter(c -> c.getStatus() == Status.Active)
                .collect(Collectors.toList());
    }

    /**
     * Lambda 3: Lọc khóa học theo status
     */
    public List<Course> getCoursesByStatus(Status status) {
        return courseDAO.findAll().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }
}
