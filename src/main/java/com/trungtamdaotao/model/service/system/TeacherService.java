package com.trungtamdaotao.model.service.system;

import com.trungtamdaotao.model.dao.system.ITeacherDAO;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.Status;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeacherService {
    private final ITeacherDAO teacherDAO;

    public TeacherService(ITeacherDAO teacherDAO) {
        this.teacherDAO = teacherDAO;
    }

    /**
     * Lambda 4: Lọc giáo viên theo chuyên môn (IELTS, TOEIC...) sử dụng hàm DAO đặc thù
     */
    public List<Teacher> getTeachersBySpecialty(String specialty) {
        return teacherDAO.findBySpecialty(specialty).stream()
                .filter(t -> t.getStatus() == Status.Active)
                .toList();
    }

    /**
     * Lambda 5: Thống kê số lượng giáo viên theo từng chuyên môn (Grouping)
     */
    public Map<String, Long> getTeacherStatsBySpecialty() {
        return teacherDAO.findAll().stream()
                .collect(Collectors.groupingBy(Teacher::getSpecialty, Collectors.counting()));
    }

    /**
     * Lambda 6: Lấy danh sách Email của các giáo viên để gửi thông báo hệ thống
     */
    public List<String> getTeacherEmails() {
        return teacherDAO.findAll().stream()
                .filter(t -> t.getStatus() == Status.Active)
                .map(Teacher::getEmail)
                .filter(email -> email != null && !email.isEmpty())
                .distinct()
                .toList();
    }
}