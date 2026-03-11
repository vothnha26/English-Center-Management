package com.trungtamdaotao.model.dao.finance.impl;

import java.util.List;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.finance.IInvoiceDAO;
import com.trungtamdaotao.model.entity.finance.Invoice;
import com.trungtamdaotao.util.DbManager;

import jakarta.persistence.EntityManager;

public class InvoiceDAOImpl extends AbstractDAO<Invoice> implements IInvoiceDAO {

    public InvoiceDAOImpl() {
        super(Invoice.class);
    }

    @Override
    public List<Invoice> findByStudentId(Long studentId) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT i FROM Invoice i WHERE i.student.student_id = :sid";
            return em.createQuery(jpql, Invoice.class)
                     .setParameter("sid", studentId)
                     .getResultList();
        }
    }

    @Override
    public List<Invoice> findByStatus(String status) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT i FROM Invoice i WHERE i.status = :status";
            return em.createQuery(jpql, Invoice.class)
                     .setParameter("status", com.trungtamdaotao.model.entity.enums.InvoiceStatus.valueOf(status))
                     .getResultList();
        }
    }
}
