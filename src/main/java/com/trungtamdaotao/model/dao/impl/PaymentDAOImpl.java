package com.trungtamdaotao.model.dao.impl;

import java.util.List;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.finance.IPaymentDAO;
import com.trungtamdaotao.model.entity.finance.Payment;
import com.trungtamdaotao.util.DbManager;

import jakarta.persistence.EntityManager;

public class PaymentDAOImpl extends AbstractDAO<Payment> implements IPaymentDAO {

    public PaymentDAOImpl() {
        super(Payment.class);
    }

    @Override
    public List<Payment> findByInvoiceId(Long invoiceId) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT p FROM Payment p WHERE p.invoice.invoice_id = :iid";
            return em.createQuery(jpql, Payment.class)
                     .setParameter("iid", invoiceId)
                     .getResultList();
        }
    }

    @Override
    public List<Payment> findByStudentId(Long studentId) {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT p FROM Payment p WHERE p.student.student_id = :sid";
            return em.createQuery(jpql, Payment.class)
                     .setParameter("sid", studentId)
                     .getResultList();
        }
    }
}
