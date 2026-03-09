package com.trungtamdaotao.view.finance;

import com.trungtamdaotao.controller.finance.FinanceController;
import com.trungtamdaotao.model.entity.finance.Payment;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FinanceReportFrame extends BaseManagerFrame {

    private final FinanceController controller;

    private JSpinner spinYear;
    private JTable tblMonthly, tblAllPayments;
    private DefaultTableModel monthlyModel, allPayModel;
    private JLabel lblTotalYear, lblTotalAll;

    public FinanceReportFrame() {
        super("Báo cáo Tài chính");
        this.controller = new FinanceController();
        loadAllPayments();
        doLoadMonthly();
    }

    @Override
    protected void initComponents() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIHelper.BOLD_FONT);

        tabs.addTab("📅 Doanh thu theo tháng", buildMonthlyTab());
        tabs.addTab("📋 Chi tiết giao dịch", buildAllPayTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildMonthlyTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(UIHelper.BACKGROUND_COLOR);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.setBackground(UIHelper.PRIMARY_COLOR);

        JLabel lblYear = new JLabel("Năm:");
        lblYear.setForeground(Color.WHITE);
        lblYear.setFont(UIHelper.BOLD_FONT);
        top.add(lblYear);

        spinYear = new JSpinner(new SpinnerNumberModel(LocalDateTime.now().getYear(), 2000, 2100, 1));
        spinYear.setPreferredSize(new Dimension(80, 30));
        top.add(spinYear);

        JButton btnLoad = UIHelper.createStandardButton("Xem báo cáo", Color.WHITE, "📊");
        btnLoad.setForeground(UIHelper.PRIMARY_COLOR);
        btnLoad.addActionListener(e -> doLoadMonthly());
        top.add(btnLoad);

        lblTotalYear = new JLabel("Tổng năm: 0 VNĐ");
        lblTotalYear.setForeground(Color.WHITE);
        lblTotalYear.setFont(UIHelper.BOLD_FONT);
        top.add(Box.createHorizontalStrut(30));
        top.add(lblTotalYear);

        monthlyModel = new DefaultTableModel(new String[]{"Tháng", "Doanh thu (VNĐ)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMonthly = new JTable(monthlyModel);
        setupTable(tblMonthly);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(tblMonthly), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildAllPayTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(UIHelper.BACKGROUND_COLOR);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.setBackground(UIHelper.PRIMARY_COLOR);

        JButton btnRefresh = UIHelper.createStandardButton("Cập nhật", Color.WHITE, "🔄");
        btnRefresh.setForeground(UIHelper.PRIMARY_COLOR);
        btnRefresh.addActionListener(e -> loadAllPayments());
        top.add(btnRefresh);

        lblTotalAll = new JLabel("Tổng doanh thu: 0 VNĐ");
        lblTotalAll.setForeground(Color.WHITE);
        lblTotalAll.setFont(UIHelper.BOLD_FONT);
        top.add(Box.createHorizontalStrut(30));
        top.add(lblTotalAll);

        allPayModel = new DefaultTableModel(new String[]{"ID", "Học viên", "Hóa đơn", "Số tiền", "Phương thức", "Ngày TT"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAllPayments = new JTable(allPayModel);
        setupTable(tblAllPayments);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(tblAllPayments), BorderLayout.CENTER);
        return p;
    }

    @Override protected void handleEvents() {}

    @Override protected void loadTableData() {}

    private void doLoadMonthly() {
        int year = (int) spinYear.getValue();
        Map<String, BigDecimal> data = controller.getMonthlyRevenue(year);
        monthlyModel.setRowCount(0);
        BigDecimal yearTotal = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : data.entrySet()) {
            monthlyModel.addRow(new Object[]{ entry.getKey(), String.format("%,.0f", entry.getValue()) });
            yearTotal = yearTotal.add(entry.getValue());
        }
        lblTotalYear.setText(String.format("Tổng năm %d: %,.0f VNĐ", year, yearTotal));
    }

    private void loadAllPayments() {
        allPayModel.setRowCount(0);
        List<Payment> list = controller.getAllPayments();
        for (Payment p : list) {
            allPayModel.addRow(new Object[]{
                p.getPaymentId(), p.getStudent().getFullName(),
                p.getInvoice() != null ? p.getInvoice().getInvoiceId() : "-",
                String.format("%,.0f", p.getAmount()), p.getPaymentMethod(), p.getPaymentDate()
            });
        }
        lblTotalAll.setText(String.format("Tổng doanh thu: %,.0f VNĐ", controller.getTotalRevenue()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FinanceReportFrame().setVisible(true));
    }
}
