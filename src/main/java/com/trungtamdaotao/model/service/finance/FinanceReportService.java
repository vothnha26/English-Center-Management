package com.trungtamdaotao.model.service.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.trungtamdaotao.model.dao.finance.IPaymentDAO;
import com.trungtamdaotao.model.entity.enums.PaymentStatus;
import com.trungtamdaotao.model.entity.finance.Payment;

public class FinanceReportService {

    private final IPaymentDAO paymentDAO;

    public FinanceReportService(IPaymentDAO paymentDAO) {
        this.paymentDAO = paymentDAO;
    }

    /** Tổng doanh thu (toàn bộ) */
    public BigDecimal getTotalRevenue() {
        return paymentDAO.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.Completed)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Doanh thu trong khoảng thời gian */
    public BigDecimal getRevenueBetween(LocalDateTime from, LocalDateTime to) {
        return paymentDAO.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.Completed)
                .filter(p -> !p.getPaymentDate().isBefore(from) && !p.getPaymentDate().isAfter(to))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Doanh thu theo tháng trong năm.
     * Trả về Map<"YYYY-MM", tổng tiền>  (sắp xếp theo thứ tự thời gian)
     */
    public Map<String, BigDecimal> getMonthlyRevenue(int year) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        // Khởi tạo 12 tháng = 0
        for (int m = 1; m <= 12; m++) {
            result.put(year + "-" + String.format("%02d", m), BigDecimal.ZERO);
        }
        paymentDAO.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.Completed)
                .filter(p -> p.getPaymentDate().getYear() == year)
                .forEach(p -> {
                    String key = year + "-" + String.format("%02d", p.getPaymentDate().getMonthValue());
                    result.merge(key, p.getAmount(), BigDecimal::add);
                });
        return result;
    }

    /** Danh sách tất cả thanh toán (để hiển thị bảng chi tiết) */
    public List<Payment> getAllPayments() {
        return paymentDAO.findAll();
    }
}
