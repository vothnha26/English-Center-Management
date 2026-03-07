package com.trungtamdaotao.model.dao.system.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.system.IStaffDAO;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;

import java.util.List;

public class StaffDAOImpl extends AbstractDAO<Staff> implements IStaffDAO {
    public StaffDAOImpl() {
        super(Staff.class);
    }

    @Override
    public List<Staff> findByRole(String role) {
        // 1. Lấy EntityManager từ DbConnection
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {

            // 2. Viết câu lệnh JPQL (Lưu ý: "Teacher" là tên Class Entity, không phải tên bảng MySQL)
            String jpql = "SELECT s FROM Staff s WHERE s.role = :role";

            // 3. Thực thi truy vấn và truyền tham số :spec để chống SQL Injection
            return em.createQuery(jpql, Staff.class)
                    .setParameter("role", role)
                    .getResultList();
        }
    }
}