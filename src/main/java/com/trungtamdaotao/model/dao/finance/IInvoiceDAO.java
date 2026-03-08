package com.trungtamdaotao.model.dao.finance;

import java.util.List;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.finance.Invoice;

public interface IInvoiceDAO extends IGenericDAO<Invoice> {
    /** Tất cả hóa đơn của một học viên */
    List<Invoice> findByStudentId(Long studentId);
    /** Hóa đơn theo trạng thái (Issued, Paid, Cancelled) */
    List<Invoice> findByStatus(String status);
}
