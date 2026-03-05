package com.trungtamdaotao.model.service.system.teacher;

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

    // CREATE & UPDATE
    public void saveOrUpdate(Teacher teacher) {
        teacherDAO.save(teacher);
    }

    // DELETE
    public void deleteTeacher(Long id) {
        teacherDAO.delete(id);
    }

    // READ ALL
    public List<Teacher> getAllTeachers() {
        return teacherDAO.findAll();
    }

    // TRA CỨU ĐA NĂNG (Họ tên, Email, Chuyên môn)
    public List<Teacher> searchTeachers(String keyword) {
        String lowerKey = keyword.toLowerCase();
        return teacherDAO.findAll().stream()
                .filter(t -> t.getFullName().toLowerCase().contains(lowerKey)
                        || t.getEmail().toLowerCase().contains(lowerKey)
                        || t.getSpecialty().toLowerCase().contains(lowerKey))
                .toList();
    }

    /**
     * Lọc giáo viên theo chuyên môn (IELTS, TOEIC...)
     */
    public List<Teacher> getTeachersBySpecialty(String specialty) {
        return teacherDAO.findBySpecialty(specialty).stream()
                .filter(t -> t.getStatus() == Status.Active)
                .toList();
    }

    /**
     * Thống kê số lượng giáo viên theo từng chuyên môn (Grouping)
     */
    public Map<String, Long> getTeacherStatsBySpecialty() {
        return teacherDAO.findAll().stream()
                .collect(Collectors.groupingBy(Teacher::getSpecialty, Collectors.counting()));
    }

    /**
     * Lấy danh sách Email của các giáo viên để gửi thông báo hệ thống
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