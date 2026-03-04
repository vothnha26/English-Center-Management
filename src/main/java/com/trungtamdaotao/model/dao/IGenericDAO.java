package com.trungtamdaotao.model.dao;

import java.util.List;

public interface IGenericDAO<T> {
    List<T> findAll();
    T findById(int id);
    void save(T entity);
    void update(T entity);
    void delete(int id);
}