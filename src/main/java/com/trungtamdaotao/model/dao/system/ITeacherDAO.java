package com.trungtamdaotao.model.dao.system;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.core.Teacher;

import java.util.List;

public interface ITeacherDAO extends IGenericDAO<Teacher> {
    List<Teacher> findBySpecialty(String spec);
}
