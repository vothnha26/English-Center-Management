package com.trungtamdaotao.model.service.student;

import com.trungtamdaotao.model.dao.student.IEnrollmentDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Enrollment;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.EnrollmentStatus;
import java.util.List;

public class EnrollmentService {

    private final IEnrollmentDAO enrollmentDAO;

    public EnrollmentService(IEnrollmentDAO enrollmentDAO) {
        this.enrollmentDAO = enrollmentDAO;
    }

    /**
     * Ghi danh học viên vào lớp.
     * @throws IllegalStateException nếu học viên đã theo học lớp này rồi
     */
    public void enroll(Student student, ClassEntity clazz) {
        if (enrollmentDAO.existsByStudentAndClass(student.getStudentId(), clazz.getClassId())) {
            throw new IllegalStateException(
                "Học viên \"" + student.getFullName() + "\" đã được ghi danh vào lớp này rồi.");
        }
        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setClazz(clazz);
        enrollmentDAO.save(e);
    }

    /** Huỷ ghi danh (đặt trạng thái Cancelled) */
    public void cancelEnrollment(int enrollmentId) {
        Enrollment e = enrollmentDAO.findById(enrollmentId);
        if (e == null) throw new IllegalArgumentException("Không tìm thấy bản ghi ghi danh.");
        e.setStatus(EnrollmentStatus.Cancelled);
        enrollmentDAO.update(e);
    }

    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentDAO.findByStudentId(studentId);
    }

    public List<Enrollment> getEnrollmentsByClass(Long classId) {
        return enrollmentDAO.findByClassId(classId);
    }

    public List<Enrollment> getAll() {
        return enrollmentDAO.findAll();
    }
}
