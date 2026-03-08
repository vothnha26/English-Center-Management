package com.trungtamdaotao.model.service.teacher;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.trungtamdaotao.model.dao.teacher.ITeacherDAO;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.Status;

public class TeacherService {

    private final ITeacherDAO teacherDAO;

    public TeacherService(ITeacherDAO teacherDAO) {
        this.teacherDAO = teacherDAO;
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    public List<Teacher> getAllTeachers() {
        return teacherDAO.findAll();
    }

    /** Chỉ hiển thị giáo viên đang Active */
    public List<Teacher> getActiveTeachers() {
        return teacherDAO.findAll().stream()
                .filter(t -> t.getStatus() == Status.Active)
                .collect(Collectors.toList());
    }

    /** Tìm kiếm theo tên hoặc số điện thoại */
    public List<Teacher> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllTeachers();
        return teacherDAO.findByNameOrPhone(keyword.trim());
    }

    public Teacher findById(Long id) {
        return teacherDAO.findById(id);
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Thêm giáo viên mới.
     * @throws IllegalArgumentException nếu thiếu họ tên hoặc số điện thoại
     */
    public void addTeacher(String fullName, String phone, String email,
                           String specialty, LocalDate hireDate) {
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Họ tên không được để trống.");
        if (phone == null || phone.isBlank())
            throw new IllegalArgumentException("Số điện thoại không được để trống.");

        Teacher t = new Teacher();
        t.setFullName(fullName.trim());
        t.setPhone(phone.trim());
        t.setEmail(email);
        t.setSpecialty(specialty);
        t.setHireDate(hireDate != null ? hireDate : LocalDate.now());
        teacherDAO.save(t);
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    public void updateTeacher(Teacher teacher) {
        if (teacher.getFullName() == null || teacher.getFullName().isBlank())
            throw new IllegalArgumentException("Họ tên không được để trống.");
        teacherDAO.update(teacher);
    }

    // ─── SOFT DELETE (đặt trạng thái Inactive thay vì xóa thật) ───────────────

    public void deactivateTeacher(Long id) {
        Teacher t = teacherDAO.findById(id);
        if (t == null) throw new IllegalArgumentException("Không tìm thấy giáo viên id=" + id);
        t.setStatus(Status.Inactive);
        teacherDAO.update(t);
    }
}