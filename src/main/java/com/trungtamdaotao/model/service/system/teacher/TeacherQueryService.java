package com.trungtamdaotao.model.service.system.teacher;

import com.trungtamdaotao.model.dao.system.ITeacherDAO;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.Status;
import java.util.List;

public class TeacherQueryService {
    private final ITeacherDAO teacherDAO;

    public TeacherQueryService(ITeacherDAO teacherDAO) {
        this.teacherDAO = teacherDAO;
    }

    public List<Teacher> findAll() {
        return teacherDAO.findAll();
    }

    public List<Teacher> searchByKeyword(String keyword) {
        String key = keyword.toLowerCase();
        return teacherDAO.findAll().stream()
                .filter(t -> t.getFullName().toLowerCase().contains(key) ||
                        t.getEmail().toLowerCase().contains(key))
                .toList();
    }

    public List<Teacher> filterBySpecialty(String specialty) {
        return teacherDAO.findAll().stream()
                .filter(t -> t.getSpecialty().equalsIgnoreCase(specialty))
                .toList();
    }

    public List<Teacher> filterByStatus(Status status) {
        return teacherDAO.findAll().stream()
                .filter(t -> t.getStatus() == status)
                .toList();
    }
}