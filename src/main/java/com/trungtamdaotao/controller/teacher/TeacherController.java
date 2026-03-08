package com.trungtamdaotao.controller.teacher;

import java.time.LocalDate;
import java.util.List;

import com.trungtamdaotao.model.dao.impl.TeacherDAOImpl;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.service.teacher.TeacherService;

public class TeacherController {

    private final TeacherService teacherService;

    /** DIP-compliant: nhận Service từ bên ngoài (dễ test, dễ thay thế impl) */
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /** Convenience constructor — tự khởi tạo impl mặc định khi không có DI framework */
    public TeacherController() {
        this(new TeacherService(new TeacherDAOImpl()));
    }

    // ─── Giáo viên ──────────────────────────────────────────────────────────────

    public List<Teacher> getAllTeachers() {
        return teacherService.getActiveTeachers();
    }

    public List<Teacher> searchTeachers(String keyword) {
        return teacherService.search(keyword);
    }

    public Teacher getTeacherById(Long id) {
        return teacherService.findById(id);
    }

    public void addTeacher(String fullName, String phone, String email,
                           String specialty, LocalDate hireDate) throws Exception {
        teacherService.addTeacher(fullName, phone, email, specialty, hireDate);
    }

    public void updateTeacher(Teacher teacher) {
        teacherService.updateTeacher(teacher);
    }

    /** Xóa mềm: chuyển trạng thái → Inactive */
    public void deleteTeacher(Long id) {
        teacherService.deactivateTeacher(id);
    }
}