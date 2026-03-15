package com.trungtamdaotao.model.dao.finance;

import java.util.List;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.finance.Payment;

public interface IPaymentDAO extends IGenericDAO<Payment> {
    /** Tất cả giao dịch thanh toán của một hóa đơn */
    List<Payment> findByInvoiceId(Long invoiceId);
    /** Tất cả giao dịch thanh toán của một học viên */
    List<Payment> findByStudentId(Long studentId);
}
