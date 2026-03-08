package com.trungtamdaotao.model.service.student;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.trungtamdaotao.model.dao.student.IStudentDAO;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.Status;

public class StudentService {

    private final IStudentDAO studentDAO;

    public StudentService(IStudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    /** Chỉ hiển thị học viên đang Active */
    public List<Student> getActiveStudents() {
        return studentDAO.findAll().stream()
                .filter(s -> s.getStatus() == Status.Active)
                .collect(Collectors.toList());
    }

    /** Tìm kiếm theo tên hoặc số điện thoại */
    public List<Student> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllStudents();
        return studentDAO.findByNameOrPhone(keyword.trim());
    }

    public Student findById(Long id) {
        return studentDAO.findById(id);
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Thêm học viên mới.
     * @throws IllegalArgumentException nếu thiếu họ tên hoặc số điện thoại
     */
    public void addStudent(String fullName, String phone, String email,
                           String address, LocalDate dob) {
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Họ tên không được để trống.");
        if (phone == null || phone.isBlank())
            throw new IllegalArgumentException("Số điện thoại không được để trống.");

        Student s = new Student();
        s.setFullName(fullName.trim());
        s.setPhone(phone.trim());
        s.setEmail(email);
        s.setAddress(address);
        s.setDateOfBirth(dob);
        s.setRegistrationDate(LocalDate.now());
        studentDAO.save(s);
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    public void updateStudent(Student student) {
        if (student.getFullName() == null || student.getFullName().isBlank())
            throw new IllegalArgumentException("Họ tên không được để trống.");
        studentDAO.update(student);
    }

    // ─── SOFT DELETE (đặt trạng thái Inactive thay vì xóa thật) ───────────────

    public void deactivateStudent(Long id) {
        Student s = studentDAO.findById(id);
        if (s == null) throw new IllegalArgumentException("Không tìm thấy học viên id=" + id);
        s.setStatus(Status.Inactive);
        studentDAO.update(s);
    }
}
