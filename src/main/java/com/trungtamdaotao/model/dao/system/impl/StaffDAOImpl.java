package com.trungtamdaotao.model.dao.system.impl;

import java.util.List;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.system.IStaffDAO;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.util.DbManager;

import jakarta.persistence.EntityManager;

public class StaffDAOImpl extends AbstractDAO<Staff> implements IStaffDAO {

    public StaffDAOImpl() {
        super(Staff.class);
    }

    @Override
    public List<Staff> findByNameOrPhone(String keyword) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT s FROM Staff s " +
                          "WHERE LOWER(s.fullName) LIKE :kw OR s.phone LIKE :kw";
            String param = "%" + keyword.toLowerCase() + "%";
            return em.createQuery(jpql, Staff.class)
                     .setParameter("kw", param)
                     .getResultList();
        }
    }
}
