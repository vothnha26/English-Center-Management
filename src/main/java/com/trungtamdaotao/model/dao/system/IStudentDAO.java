package com.trungtamdaotao.model.dao.system;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.core.Student;

import java.util.List;

public interface IStudentDAO extends IGenericDAO<Student> {
    List<Student> findByStatus(String status);
}