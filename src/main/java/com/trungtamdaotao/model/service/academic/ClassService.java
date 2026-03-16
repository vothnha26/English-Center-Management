package com.trungtamdaotao.model.service.academic;

import com.trungtamdaotao.model.dao.academic.IClassDAO;
import com.trungtamdaotao.model.dao.student.IEnrollmentDAO;
import com.trungtamdaotao.model.dao.student.impl.EnrollmentDAOImpl;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Course;

import java.util.List;
import java.util.stream.Collectors;

public class ClassService {
    private final IClassDAO classDAO;
    private final IEnrollmentDAO enrollmentDAO;

    public ClassService(IClassDAO classDAO) {
        this(classDAO, new EnrollmentDAOImpl());
    }

    public ClassService(IClassDAO classDAO, IEnrollmentDAO enrollmentDAO) {
        this.classDAO = classDAO;
        this.enrollmentDAO = enrollmentDAO;
    }

    /**
     * Tạo mới lớp học với validation
     */
    public void createClass(ClassEntity classEntity) {
        // Validation 1: className không được rỗng
        if (classEntity.getClassName() == null || classEntity.getClassName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên lớp học không được để trống!");
        }

        // Validation 2: Kiểm tra trùng tên lớp học
        ClassEntity existing = classDAO.findByClassName(classEntity.getClassName());
        if (existing != null) {
            throw new IllegalArgumentException("Lớp học '" + classEntity.getClassName() + "' đã tồn tại!");
        }

        // Validation 3: Course không được null
        if (classEntity.getCourse() == null) {
            throw new IllegalArgumentException("Phải chọn khóa học cho lớp!");
        }

        // Validation 4: maxStudent phải > 0
        if (classEntity.getMaxStudent() <= 0) {
            throw new IllegalArgumentException("Số lượng học viên tối đa phải lớn hơn 0!");
        }

        // Validation 5: startDate <= endDate (nếu cả 2 đều có)
        if (classEntity.getStartDate() != null && classEntity.getEndDate() != null) {
            if (classEntity.getStartDate().isAfter(classEntity.getEndDate())) {
                throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc!");
            }
        }

        classDAO.save(classEntity);
    }

    /**
     * Cập nhật lớp học
     */
    public void updateClass(ClassEntity classEntity) {
        // Validation tương tự create
        if (classEntity.getClassName() == null || classEntity.getClassName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên lớp học không được để trống!");
        }

        if (classEntity.getCourse() == null) {
            throw new IllegalArgumentException("Phải chọn khóa học cho lớp!");
        }

        if (classEntity.getMaxStudent() <= 0) {
            throw new IllegalArgumentException("Số lượng học viên tối đa phải lớn hơn 0!");
        }

        if (classEntity.getStartDate() != null && classEntity.getEndDate() != null) {
            if (classEntity.getStartDate().isAfter(classEntity.getEndDate())) {
                throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc!");
            }
        }

        // Kiểm tra trùng tên (ngoại trừ chính nó)
        ClassEntity existing = classDAO.findByClassName(classEntity.getClassName());
        if (existing != null && !existing.getClass_id().equals(classEntity.getClass_id())) {
            throw new IllegalArgumentException("Lớp học '" + classEntity.getClassName() + "' đã tồn tại!");
        }

        classDAO.update(classEntity);
    }

    /**
     * Xóa lớp học
     */
    public void deleteClass(int id) {
        if (!enrollmentDAO.findByClassId((long) id).isEmpty()) {
            throw new IllegalStateException("Không thể xóa lớp vì đang có học viên ghi danh. Hãy hủy/điều chuyển ghi danh trước.");
        }
        classDAO.delete((long) id);
    }

    /**
     * Lấy tất cả lớp học
     */
    public List<ClassEntity> getAllClasses() {
        return classDAO.findAll();
    }

    /**
     * Lấy lớp học theo ID
     */
    public ClassEntity getClassById(int id) {
        return classDAO.findByIdWithRelations(id);
    }

    /**
     * Lambda 1: Tìm kiếm lớp học theo tên
     */
    public List<ClassEntity> searchClasses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return classDAO.findAll();
        }
        String lowerKey = keyword.toLowerCase();
        return classDAO.findAll().stream()
                .filter(c -> c.getClassName().toLowerCase().contains(lowerKey))
                .collect(Collectors.toList());
    }

    /**
     * Lambda 2: Lọc lớp học theo khóa học
     */
    public List<ClassEntity> getClassesByCourse(Course course) {
        if (course == null) {
            return classDAO.findAll();
        }
        return classDAO.findAll().stream()
                .filter(c -> c.getCourse() != null && c.getCourse().equals(course))
                .collect(Collectors.toList());
    }

    /**
     * Lambda 3: Lọc lớp học theo courseId
     */
    public List<ClassEntity> getClassesByCourseId(Long courseId) {
        if (courseId == null) {
            return classDAO.findAll();
        }
        return classDAO.findAll().stream()
                .filter(c -> c.getCourse() != null && c.getCourse().getCourse_id().equals(courseId))
                .toList();
    }
}
