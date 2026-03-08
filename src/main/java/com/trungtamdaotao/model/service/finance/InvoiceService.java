package com.trungtamdaotao.model.service.finance;

import java.math.BigDecimal;
import java.util.List;

import com.trungtamdaotao.model.dao.finance.IInvoiceDAO;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.InvoiceStatus;
import com.trungtamdaotao.model.entity.finance.Invoice;

public class InvoiceService {

    private final IInvoiceDAO invoiceDAO;

    public InvoiceService(IInvoiceDAO invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
    }

    /**
     * Tạo hóa đơn mới cho học viên.
     * @param amount  Số tiền học phí (> 0)
     * @param note    Ghi chú (có thể null)
     */
    public Invoice createInvoice(Student student, BigDecimal amount, String note) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Số tiền hóa đơn phải lớn hơn 0.");

        Invoice inv = new Invoice();
        inv.setStudent(student);
        inv.setTotalAmount(amount);
        inv.setNote(note);
        inv.setStatus(InvoiceStatus.Issued);
        invoiceDAO.save(inv);
        return inv;
    }

    /** Cập nhật trạng thái hóa đơn (Issued → Paid hoặc Cancelled) */
    public void updateStatus(Long invoiceId, InvoiceStatus newStatus) {
        Invoice inv = invoiceDAO.findById(invoiceId);
        if (inv == null) throw new IllegalArgumentException("Không tìm thấy hóa đơn id=" + invoiceId);
        inv.setStatus(newStatus);
        invoiceDAO.update(inv);
    }

    public List<Invoice> getAll() {
        return invoiceDAO.findAll();
    }

    public List<Invoice> getByStudent(Long studentId) {
        return invoiceDAO.findByStudentId(studentId);
    }

    public List<Invoice> getUnpaid() {
        return invoiceDAO.findByStatus("Issued");
    }
}
