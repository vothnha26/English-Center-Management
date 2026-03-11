package com.trungtamdaotao.controller.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.trungtamdaotao.model.dao.impl.InvoiceDAOImpl;
import com.trungtamdaotao.model.dao.impl.PaymentDAOImpl;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.InvoiceStatus;
import com.trungtamdaotao.model.entity.enums.PaymentMethod;
import com.trungtamdaotao.model.entity.finance.Invoice;
import com.trungtamdaotao.model.entity.finance.Payment;
import com.trungtamdaotao.model.service.finance.FinanceReportService;
import com.trungtamdaotao.model.service.finance.InvoiceService;
import com.trungtamdaotao.model.service.finance.PaymentService;

public class FinanceController {

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final FinanceReportService reportService;

    /** DIP-compliant: nhận Service từ bên ngoài (dễ test, dễ thay thế impl) */
    public FinanceController(InvoiceService invoiceService,
                             PaymentService paymentService,
                             FinanceReportService reportService) {
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
        this.reportService  = reportService;
    }

    /** Convenience constructor — tự khởi tạo impl mặc định khi không có DI framework */
    public FinanceController() {
        InvoiceDAOImpl invoiceDAO = new InvoiceDAOImpl();
        PaymentDAOImpl paymentDAO = new PaymentDAOImpl();
        this.invoiceService = new InvoiceService(invoiceDAO);
        this.paymentService = new PaymentService(paymentDAO, invoiceDAO);
        this.reportService  = new FinanceReportService(paymentDAO);
    }

    // ─── Hóa đơn ───────────────────────────────────────────────────────────────

    public Invoice createInvoice(Student student, BigDecimal amount, String note) {
        return invoiceService.createInvoice(student, amount, note);
    }

    public void cancelInvoice(Long invoiceId) {
        invoiceService.updateStatus(invoiceId, InvoiceStatus.Cancelled);
    }

    public String deleteInvoice(Long invoiceId) {
        try {
            invoiceService.deleteInvoice(invoiceId);
            return "Xóa hóa đơn thành công!";
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }

    public String updateInvoiceStatus(Long invoiceId, InvoiceStatus status) {
        try {
            invoiceService.updateStatus(invoiceId, status);
            return "Cập nhật trạng thái thành công!";
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }

    public List<Invoice> getAllInvoices() {
        return invoiceService.getAll();
    }

    public List<Invoice> getInvoicesByStudent(Long studentId) {
        return invoiceService.getByStudent(studentId);
    }

    public List<Invoice> getUnpaidInvoices() {
        return invoiceService.getUnpaid();
    }

    // ─── Thanh toán ────────────────────────────────────────────────────────────

    public void recordPayment(Invoice invoice, BigDecimal amount,
                              PaymentMethod method, String ref) {
        paymentService.recordPayment(invoice, amount, method, ref);
    }

    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        return paymentService.getByInvoice(invoiceId);
    }

    // ─── Báo cáo ───────────────────────────────────────────────────────────────

    public BigDecimal getTotalRevenue() {
        return reportService.getTotalRevenue();
    }

    public BigDecimal getRevenueBetween(LocalDateTime from, LocalDateTime to) {
        return reportService.getRevenueBetween(from, to);
    }

    public Map<String, BigDecimal> getMonthlyRevenue(int year) {
        return reportService.getMonthlyRevenue(year);
    }

    public List<Payment> getAllPayments() {
        return reportService.getAllPayments();
    }
}
