package com.trungtamdaotao.view.finance;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.trungtamdaotao.controller.finance.FinanceController;
import com.trungtamdaotao.model.entity.finance.Payment;

/**
 * Màn hình Báo cáo Tài chính.
 * Tab 1: Doanh thu theo tháng (bảng dữ liệu)
 * Tab 2: Tất cả giao dịch thanh toán
 */
public class FinanceReportFrame extends JFrame {

    private final FinanceController controller;

    // Tab 1 - theo tháng
    private JSpinner spinYear;
    private JTable   tblMonthly;
    private DefaultTableModel monthlyModel;
    private JLabel   lblTotalYear;

    // Tab 2 - chi tiết giao dịch
    private JTable tblAllPayments;
    private DefaultTableModel allPayModel;
    private JLabel lblTotalAll;

    private static final String[] MONTHLY_COLS   = {"Tháng", "Doanh thu (VNĐ)"};
    private static final String[] ALL_PAY_COLS   = {"ID", "Học viên", "Hóa đơn", "Số tiền",
                                                    "Phương thức", "Ngày thanh toán", "Mã TK"};

    public FinanceReportFrame() {
        this.controller = new FinanceController();
        initUI();
        loadAllPayments();
        doLoadMonthly();
    }

    private void initUI() {
        setTitle("Báo cáo Tài chính");
        setSize(860, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("📅 Doanh thu theo tháng", buildMonthlyTab());
        tabs.addTab("📋 Chi tiết giao dịch",   buildAllPayTab());
        add(tabs);
    }

    // ── Tab 1: doanh thu theo tháng ──────────────────────────────────────────
    private JPanel buildMonthlyTab() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Top: chọn năm + nút xem
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        spinYear = new JSpinner(new SpinnerNumberModel(LocalDateTime.now().getYear(), 2000, 2100, 1));
        spinYear.setPreferredSize(new Dimension(80, 26));
        JButton btnLoad = new JButton("📊 Xem báo cáo");
        btnLoad.addActionListener(e -> doLoadMonthly());
        lblTotalYear = new JLabel("Tổng năm: 0 VNĐ");
        lblTotalYear.setFont(lblTotalYear.getFont().deriveFont(Font.BOLD, 13f));

        top.add(new JLabel("Năm:")); top.add(spinYear);
        top.add(btnLoad); top.add(Box.createHorizontalStrut(20)); top.add(lblTotalYear);

        // Bảng
        monthlyModel = new DefaultTableModel(MONTHLY_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? BigDecimal.class : String.class; }
        };
        tblMonthly = new JTable(monthlyModel);
        tblMonthly.setRowHeight(24);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(tblMonthly), BorderLayout.CENTER);
        return p;
    }

    // ── Tab 2: tất cả giao dịch ──────────────────────────────────────────────
    private JPanel buildAllPayTab() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("🔄 Cập nhật");
        lblTotalAll = new JLabel();
        lblTotalAll.setFont(lblTotalAll.getFont().deriveFont(Font.BOLD, 13f));
        btnRefresh.addActionListener(e -> loadAllPayments());
        top.add(btnRefresh); top.add(Box.createHorizontalStrut(20)); top.add(lblTotalAll);

        allPayModel = new DefaultTableModel(ALL_PAY_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAllPayments = new JTable(allPayModel);
        tblAllPayments.setRowHeight(22);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(tblAllPayments), BorderLayout.CENTER);
        return p;
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void doLoadMonthly() {
        int year = (int) spinYear.getValue();
        Map<String, BigDecimal> data = controller.getMonthlyRevenue(year);

        monthlyModel.setRowCount(0);
        BigDecimal yearTotal = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : data.entrySet()) {
            monthlyModel.addRow(new Object[]{ entry.getKey(), entry.getValue() });
            yearTotal = yearTotal.add(entry.getValue());
        }
        lblTotalYear.setText(String.format("Tổng năm %d: %,.0f VNĐ", year, yearTotal));
    }

    private void loadAllPayments() {
        allPayModel.setRowCount(0);
        List<Payment> list = controller.getAllPayments();
        for (Payment p : list) {
            allPayModel.addRow(new Object[]{
                p.getPaymentId(),
                p.getStudent().getFullName(),
                p.getInvoice() != null ? p.getInvoice().getInvoiceId() : "-",
                p.getAmount(),
                p.getPaymentMethod(),
                p.getPaymentDate(),
                p.getReferenceCode()
            });
        }
        BigDecimal total = controller.getTotalRevenue();
        lblTotalAll.setText(String.format("Tổng doanh thu: %,.0f VNĐ", total));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FinanceReportFrame().setVisible(true));
    }
}
