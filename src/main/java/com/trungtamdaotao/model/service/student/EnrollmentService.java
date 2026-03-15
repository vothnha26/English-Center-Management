package com.trungtamdaotao.model.service.student;

import com.trungtamdaotao.model.dao.student.IEnrollmentDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Enrollment;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.EnrollmentStatus;
import com.trungtamdaotao.model.service.common.EmailService;
import com.trungtamdaotao.util.EmailConfig;
import jakarta.mail.MessagingException;
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
        // Không cho phép học viên đang có ghi danh Pending hoặc Enrolled ghi danh thêm lớp khác
        List<Enrollment> existing = enrollmentDAO.findByStudentId(student.getStudent_id());
        boolean hasActive = existing.stream().anyMatch(en -> en.getStatus() == EnrollmentStatus.Pending || en.getStatus() == EnrollmentStatus.Enrolled);
        if (hasActive) {
            throw new IllegalStateException("Học viên \"" + student.getFullName() + "\" chỉ được đăng ký một khóa tại một thời điểm.");
        }
        if (enrollmentDAO.existsByStudentAndClass(student.getStudent_id(), clazz.getClass_id())) {
            throw new IllegalStateException(
                "Học viên \"" + student.getFullName() + "\" đã được ghi danh vào lớp này rồi.");
        }
        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setClazz(clazz);
        e.setStatus(EnrollmentStatus.Pending);
        enrollmentDAO.save(e);
    }

    /** Huỷ ghi danh (đặt trạng thái Cancelled) */
    public void cancelEnrollment(Long enrollmentId) {
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

    public Enrollment approveEnrollment(Long enrollmentId) {
        Enrollment e = enrollmentDAO.findById(enrollmentId);
        if (e == null) throw new IllegalArgumentException("Không tìm thấy bản ghi ghi danh.");
        e.setStatus(EnrollmentStatus.Enrolled);
        enrollmentDAO.update(e);
        // Gửi email thông báo cho học viên
        try {
            String to = e.getStudent().getEmail();
            String subject = "Yêu cầu ghi danh đã được phê duyệt";
                String body = "Xin chào " + e.getStudent().getFullName() + ",<br/><br/>" +
                    "Yêu cầu ghi danh của bạn vào lớp <b>" + e.getClazz().getClassName() + "</b> đã được phê duyệt.<br/>" +
                    "Chúc bạn học tốt!<br/><br/>Trung tâm.";
            EmailService mail = new EmailService(EmailConfig.getSmtpHost(), EmailConfig.getSmtpPort(), EmailConfig.getMailUsername(), EmailConfig.getMailPassword());
            mail.sendEmail(to, subject, body);
        } catch (MessagingException | IllegalStateException ex) {
            // không block luồng chính; log ra console
            System.err.println("Không thể gửi email phê duyệt: " + ex.getMessage());
        }
        return e;
    }

    public List<Enrollment> getPendingEnrollments() {
        return enrollmentDAO.findAll().stream()
            .filter(en -> en.getStatus() == EnrollmentStatus.Pending)
            .toList();
    }
}
