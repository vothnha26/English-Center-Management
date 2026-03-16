package com.trungtamdaotao.controller.student;

import java.time.LocalDate;
import java.util.List;

import com.trungtamdaotao.model.dao.student.impl.EnrollmentDAOImpl;
import com.trungtamdaotao.model.dao.student.impl.StudentDAOImpl;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Enrollment;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.service.student.EnrollmentService;
import com.trungtamdaotao.model.service.student.StudentService;

public class StudentController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    /** DIP-compliant: nhận Service từ bên ngoài (dễ test, dễ thay thế impl) */
    public StudentController(StudentService studentService, EnrollmentService enrollmentService) {
        this.studentService    = studentService;
        this.enrollmentService = enrollmentService;
    }

    /** Convenience constructor — tự khởi tạo impl mặc định khi không có DI framework */
    public StudentController() {
        this(new StudentService(new StudentDAOImpl()),
             new EnrollmentService(new EnrollmentDAOImpl()));
    }

    // ─── Học viên ──────────────────────────────────────────────────────────────

    public List<Student> getAllStudents() {
        return studentService.getActiveStudents();
    }

    public List<Student> searchStudents(String keyword) {
        return studentService.search(keyword);
    }

    public Student getStudentById(Long id) {
        return studentService.findById(id);
    }

    public void addStudent(String fullName, String phone, String email,
                           String address, LocalDate dob) throws Exception {
        studentService.addStudent(fullName, phone, email, address, dob);
    }

    public void addStudent(String fullName, String phone, String email,
                           String address, LocalDate dob, String plainPassword) throws Exception {
        studentService.addStudent(fullName, phone, email, address, dob, plainPassword);
    }

    public void updateStudent(Student student) {
        studentService.updateStudent(student);
    }

    /** Xóa mềm: chuyển trạng thái → Inactive */
    public void deleteStudent(Long id) {
        studentService.deactivateStudent(id);
    }

    /**
     * Kích hoạt lại hoặc thay đổi trạng thái học viên
     */
    public void updateStatus(Long id, com.trungtamdaotao.model.entity.enums.Status status) {
        Student s = studentService.findById(id);
        if (s != null) {
            s.setStatus(status);
            studentService.updateStudent(s);
        }
    }

    // ─── Ghi danh ──────────────────────────────────────────────────────────────

    public void enroll(Student student, ClassEntity clazz) {
        enrollmentService.enroll(student, clazz);
    }

    public void cancelEnrollment(Long enrollmentId) {
        enrollmentService.cancelEnrollment(enrollmentId);
    }

    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentService.getEnrollmentsByStudent(studentId);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentService.getAll();
    }

    public Enrollment getEnrollmentById(Long id) {
        return enrollmentService.getAll().stream().filter(e -> e.getEnrollmentId().equals(id)).findFirst().orElse(null);
    }

    public Enrollment approveEnrollment(Long enrollmentId) {
        return enrollmentService.approveEnrollment(enrollmentId);
    }

    public List<Enrollment> getPendingEnrollments() {
        return enrollmentService.getPendingEnrollments();
    }

    public List<Enrollment> getEnrollmentsByClass(Long classId) {
        return enrollmentService.getEnrollmentsByClass(classId);
    }
}
