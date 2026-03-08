package com.trungtamdaotao.model.service.finance;

import java.math.BigDecimal;
import java.util.List;

import com.trungtamdaotao.model.dao.finance.IInvoiceDAO;
import com.trungtamdaotao.model.dao.finance.IPaymentDAO;
import com.trungtamdaotao.model.entity.enums.InvoiceStatus;
import com.trungtamdaotao.model.entity.enums.PaymentMethod;
import com.trungtamdaotao.model.entity.enums.PaymentStatus;
import com.trungtamdaotao.model.entity.finance.Invoice;
import com.trungtamdaotao.model.entity.finance.Payment;

public class PaymentService {

    private final IPaymentDAO paymentDAO;
    private final IInvoiceDAO invoiceDAO;

    public PaymentService(IPaymentDAO paymentDAO, IInvoiceDAO invoiceDAO) {
        this.paymentDAO = paymentDAO;
        this.invoiceDAO = invoiceDAO;
    }

    /**
     * Ghi nhận thanh toán cho hóa đơn.
     * Tự động đánh dấu hóa đơn là Paid khi số tiền đủ.
     */
    public void recordPayment(Invoice invoice, BigDecimal amount,
                              PaymentMethod method, String referenceCode) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0.");

        Payment p = new Payment();
        p.setStudent(invoice.getStudent());
        p.setInvoice(invoice);
        p.setAmount(amount);
        p.setPaymentMethod(method);
        p.setReferenceCode(referenceCode);
        p.setStatus(PaymentStatus.Completed);
        paymentDAO.save(p);

        // Tính tổng đã thanh toán, nếu >= tổng hóa đơn → Paid
        BigDecimal totalPaid = paymentDAO.findByInvoiceId(invoice.getInvoiceId())
                .stream()
                .filter(pay -> pay.getStatus() == PaymentStatus.Completed)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.Paid);
            invoiceDAO.update(invoice);
        }
    }

    public List<Payment> getByInvoice(Long invoiceId) {
        return paymentDAO.findByInvoiceId(invoiceId);
    }

    public List<Payment> getAll() {
        return paymentDAO.findAll();
    }
}
