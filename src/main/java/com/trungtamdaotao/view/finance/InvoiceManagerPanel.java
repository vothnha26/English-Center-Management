package com.trungtamdaotao.view.finance;

import com.trungtamdaotao.controller.finance.FinanceController;
import com.trungtamdaotao.model.entity.finance.Invoice;
import com.trungtamdaotao.model.entity.enums.InvoiceStatus;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Quản lý hóa đơn - Đã sửa lỗi Controller và Getter.
 */
public class InvoiceManagerPanel extends BaseManagerPanel {
    private final FinanceController financeController;
    private JTabbedPane tabbedPane;
    private JTable tblInvoice;
    private DefaultTableModel tableModel;
    
    private JPanel pnlWizard;
    private CardLayout wizardLayout;

    public InvoiceManagerPanel() {
        super();
        this.financeController = new FinanceController();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIHelper.BOLD_FONT);

        tabbedPane.addTab("DANH SÁCH HÓA ĐƠN", createListPanel());
        tabbedPane.addTab("TẠO HÓA ĐƠN MỚI", createWizardPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createListPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 15));
        pnl.setOpaque(false);
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFilter.setOpaque(false);
        pnlFilter.add(new JLabel("Trạng thái:"));
        pnlFilter.add(new JComboBox<>(InvoiceStatus.values()));
        pnl.add(pnlFilter, BorderLayout.NORTH);

        String[] columns = {"ID", "Học viên", "Tổng tiền", "Ngày tạo", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblInvoice = new JTable(tableModel);
        setupTable(tblInvoice);
        
        tblInvoice.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (value != null && value.toString().contains("Unpaid")) {
                    lbl.setForeground(Color.RED);
                    lbl.setFont(UIHelper.BOLD_FONT);
                }
                return lbl;
            }
        });

        pnl.add(new JScrollPane(tblInvoice), BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createWizardPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);

        JPanel pnlProgress = new JPanel(new GridLayout(1, 3, 10, 0));
        pnlProgress.setBorder(new EmptyBorder(20, 50, 20, 50));
        pnlProgress.setOpaque(false);
        pnlProgress.add(new JLabel("BƯỚC 1: HỌC VIÊN", SwingConstants.CENTER));
        pnlProgress.add(new JLabel("BƯỚC 2: THANH TOÁN", SwingConstants.CENTER));
        pnlProgress.add(new JLabel("BƯỚC 3: HOÀN TẤT", SwingConstants.CENTER));
        pnl.add(pnlProgress, BorderLayout.NORTH);

        wizardLayout = new CardLayout();
        pnlWizard = new JPanel(wizardLayout);
        pnlWizard.setOpaque(false);
        pnlWizard.add(new JLabel("Giao diện Wizard đang được xây dựng...", SwingConstants.CENTER), "Step1");
        pnl.add(pnlWizard, BorderLayout.CENTER);

        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        pnlNav.setOpaque(false);
        pnlNav.add(UIHelper.createStandardButton("Quay lại", Color.GRAY, ""));
        pnlNav.add(UIHelper.createStandardButton("Tiếp theo", UIHelper.PRIMARY_COLOR, ""));
        pnl.add(pnlNav, BorderLayout.SOUTH);

        return pnl;
    }

    @Override protected void handleEvents() {}
    
    @Override protected void loadTableData() {
        if (financeController == null) return;
        List<Invoice> list = financeController.getAllInvoices();
        tableModel.setRowCount(0);
        for (Invoice i : list) {
            tableModel.addRow(new Object[]{ i.getInvoiceId(), i.getStudent().getFullName(), i.getTotalAmount(), i.getIssueDate(), i.getStatus() });
        }
    }
}
