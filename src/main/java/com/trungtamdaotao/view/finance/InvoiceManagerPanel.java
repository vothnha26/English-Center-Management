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

    // --- Buttons & Filters ---
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch;
    private JComboBox<InvoiceStatus> cmbStatusFilter;

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

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlFilter.setOpaque(false);
        pnlFilter.add(new JLabel("Trạng thái:"));
        cmbStatusFilter = new JComboBox<>(InvoiceStatus.values());
        pnlFilter.add(cmbStatusFilter);
        btnSearch = UIHelper.createStandardButton("Lọc", UIHelper.PRIMARY_COLOR, "🔍");
        pnlFilter.add(btnSearch);
        
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);
        btnAdd = UIHelper.createStandardButton("Tạo mới", UIHelper.SUCCESS_COLOR, "➕");
        btnUpdate = UIHelper.createStandardButton("Cập nhật trạng thái", UIHelper.PRIMARY_COLOR, "📝");
        btnDelete = UIHelper.createStandardButton("Xóa", Color.RED, "🗑");
        pnlActions.add(btnAdd); pnlActions.add(btnUpdate); pnlActions.add(btnDelete);

        pnlTop.add(pnlFilter, BorderLayout.WEST);
        pnlTop.add(pnlActions, BorderLayout.EAST);
        pnl.add(pnlTop, BorderLayout.NORTH);

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

    @Override
    protected void handleEvents() {
        btnSearch.addActionListener(e -> loadTableData());

        btnAdd.addActionListener(e -> tabbedPane.setSelectedIndex(1));

        btnUpdate.addActionListener(e -> {
            int row = tblInvoice.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để cập nhật!");
                return;
            }
            Long id = (Long) tableModel.getValueAt(row, 0);
            InvoiceStatus currentStatus = (InvoiceStatus) tableModel.getValueAt(row, 4);
            
            InvoiceStatus newStatus = (InvoiceStatus) JOptionPane.showInputDialog(
                this, "Chọn trạng thái mới:", "Cập nhật trạng thái",
                JOptionPane.QUESTION_MESSAGE, null, InvoiceStatus.values(), currentStatus
            );
            
            if (newStatus != null && newStatus != currentStatus) {
                String msg = financeController.updateInvoiceStatus(id, newStatus);
                JOptionPane.showMessageDialog(this, msg);
                loadTableData();
            }
        });

        btnDelete.addActionListener(e -> {
            int row = tblInvoice.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xóa!");
                return;
            }
            Long id = (Long) tableModel.getValueAt(row, 0);
            int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa hóa đơn này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                String msg = financeController.deleteInvoice(id);
                JOptionPane.showMessageDialog(this, msg);
                loadTableData();
            }
        });
    }
    
    @Override 
    protected void loadTableData() {
        if (financeController == null || tableModel == null) return;
        List<Invoice> list = financeController.getAllInvoices();
        InvoiceStatus filter = cmbStatusFilter != null ? (InvoiceStatus) cmbStatusFilter.getSelectedItem() : null;
        
        tableModel.setRowCount(0);
        list.stream()
            .filter(i -> filter == null || i.getStatus() == filter)
            .forEach(i -> {
                tableModel.addRow(new Object[]{ 
                    i.getInvoiceId(), 
                    i.getStudent() != null ? i.getStudent().getFullName() : "N/A", 
                    i.getTotalAmount(), 
                    i.getIssueDate(), 
                    i.getStatus() 
                });
            });
    }
}
