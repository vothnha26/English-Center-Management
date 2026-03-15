package com.trungtamdaotao.view.finance;

import com.trungtamdaotao.controller.finance.FinanceController;
import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.InvoiceStatus;
import com.trungtamdaotao.model.entity.enums.PaymentMethod;
import com.trungtamdaotao.model.entity.finance.Invoice;
import com.trungtamdaotao.model.entity.finance.Payment;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class InvoiceManagerFrame extends BaseManagerFrame {

    private final FinanceController financeCtrl;
    private final StudentController studentCtrl;

    private JTable tblInvoices, tblPayments;
    private DefaultTableModel invoiceModel, paymentModel;

    private JComboBox<Student> cmbStudent;
    private JComboBox<String> cmbStatus;
    private JTextField txtAmount, txtRef, txtSearch;
    private JComboBox<PaymentMethod> cmbMethod;
    private JButton btnFilter, btnNewInvoice, btnCancelInv, btnReload, btnPay;

    public InvoiceManagerFrame() {
        super("Quản lý Hóa đơn & Thanh toán");
        this.financeCtrl = new FinanceController();
        this.studentCtrl = new StudentController();
        loadStudentCombo();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        // --- Toolbar (NORTH) ---
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlToolbar.setBackground(UIHelper.PRIMARY_COLOR);

        JLabel lblStudent = new JLabel("Học viên:");
        lblStudent.setForeground(Color.WHITE);
        lblStudent.setFont(UIHelper.BOLD_FONT);
        pnlToolbar.add(lblStudent);

        cmbStudent = new JComboBox<>();
        pnlToolbar.add(cmbStudent);

        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(UIHelper.BOLD_FONT);
        pnlToolbar.add(lblStatus);

        cmbStatus = new JComboBox<>(new String[]{"Tất cả", "Issued", "Paid", "Cancelled"});
        pnlToolbar.add(cmbStatus);

        btnFilter = UIHelper.createStandardButton("Lọc", Color.WHITE, "🔍");
        btnFilter.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnFilter);

        btnReload = UIHelper.createStandardButton("Tải lại", Color.WHITE, "⟳");
        btnReload.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnReload);

        btnNewInvoice = UIHelper.createStandardButton("Tạo HĐ", UIHelper.SUCCESS_COLOR, "✚");
        pnlToolbar.add(btnNewInvoice);

        btnCancelInv = UIHelper.createStandardButton("Hủy HĐ", UIHelper.DANGER_COLOR, "✘");
        pnlToolbar.add(btnCancelInv);

        add(pnlToolbar, BorderLayout.NORTH);

        // --- Center Panel (Split Pane) ---
        invoiceModel = new DefaultTableModel(new String[]{"ID", "Học viên", "Số tiền", "Ngày tạo", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblInvoices = new JTable(invoiceModel);
        setupTable(tblInvoices);

        paymentModel = new DefaultTableModel(new String[]{"ID", "Số tiền", "Phương thức", "Ngày TT", "Mã TK"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPayments = new JTable(paymentModel);
        setupTable(tblPayments);

        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));
        pnlLeft.add(new JScrollPane(tblInvoices));

        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBorder(BorderFactory.createTitledBorder("Chi tiết thanh toán"));
        pnlRight.add(new JScrollPane(tblPayments));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLeft, pnlRight);
        splitPane.setDividerLocation(600);
        add(splitPane, BorderLayout.CENTER);

        // --- Payment Form (SOUTH) ---
        JPanel pnlPayment = UIHelper.createFormPanel("Ghi nhận thanh toán cho hóa đơn đang chọn");
        pnlPayment.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        pnlPayment.add(createFieldLabel("Số tiền:"));
        txtAmount = new JTextField(10);
        pnlPayment.add(txtAmount);

        pnlPayment.add(createFieldLabel("Phương thức:"));
        cmbMethod = new JComboBox<>(PaymentMethod.values());
        pnlPayment.add(cmbMethod);

        pnlPayment.add(createFieldLabel("Mã tham chiếu:"));
        txtRef = new JTextField(12);
        pnlPayment.add(txtRef);

        btnPay = UIHelper.createStandardButton("Xác nhận thanh toán", UIHelper.SUCCESS_COLOR, "💰");
        pnlPayment.add(btnPay);

        add(pnlPayment, BorderLayout.SOUTH);
    }

    @Override
    protected void handleEvents() {
        btnFilter.addActionListener(e -> doFilter());
        btnReload.addActionListener(e -> loadTableData());
        btnNewInvoice.addActionListener(e -> doCreateInvoice());
        btnCancelInv.addActionListener(e -> doCancelInvoice());
        btnPay.addActionListener(e -> doRecordPayment());

        tblInvoices.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadPaymentsForSelectedInvoice();
        });
    }

    private void loadStudentCombo() {
        cmbStudent.removeAllItems();
        cmbStudent.addItem(null);
        for (Student s : studentCtrl.getAllStudents()) cmbStudent.addItem(s);
    }

    @Override
    protected void loadTableData() {
        if (financeCtrl != null) renderInvoices(financeCtrl.getAllInvoices());
    }

    private void renderInvoices(List<Invoice> list) {
        invoiceModel.setRowCount(0);
        for (Invoice inv : list) {
            invoiceModel.addRow(new Object[]{
                inv.getInvoiceId(), inv.getStudent().getFullName(),
                String.format("%,.0f", inv.getTotalAmount()),
                inv.getIssueDate(), inv.getStatus()
            });
        }
    }

    private void loadPaymentsForSelectedInvoice() {
        paymentModel.setRowCount(0);
        int row = tblInvoices.getSelectedRow();
        if (row < 0) return;
        long invoiceId = (long) invoiceModel.getValueAt(row, 0);
        List<Payment> payments = financeCtrl.getPaymentsByInvoice(invoiceId);
        for (Payment p : payments) {
            paymentModel.addRow(new Object[]{
                p.getPaymentId(), String.format("%,.0f", p.getAmount()),
                p.getPaymentMethod(), p.getPaymentDate(), p.getReferenceCode()
            });
        }
    }

    private void doFilter() {
        Student s = (Student) cmbStudent.getSelectedItem();
        String statusStr = (String) cmbStatus.getSelectedItem();
        List<Invoice> list = (s != null) ? financeCtrl.getInvoicesByStudent(s.getStudent_id()) : financeCtrl.getAllInvoices();
        if (!"Tất cả".equals(statusStr)) {
            InvoiceStatus status = InvoiceStatus.valueOf(statusStr);
            list = list.stream().filter(i -> i.getStatus() == status).toList();
        }
        renderInvoices(list);
    }

    private void doCreateInvoice() {
        Student s = (Student) cmbStudent.getSelectedItem();
        if (s == null) { JOptionPane.showMessageDialog(this, "Chọn học viên!"); return; }
        String amt = JOptionPane.showInputDialog(this, "Số tiền học phí:");
        if (amt == null || amt.isBlank()) return;
        try {
            financeCtrl.createInvoice(s, new BigDecimal(amt), "Tạo thủ công");
            loadTableData();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    private void doCancelInvoice() {
        int row = tblInvoices.getSelectedRow();
        if (row < 0) return;
        long id = (long) invoiceModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Hủy hóa đơn này?") == JOptionPane.YES_OPTION) {
            try { financeCtrl.cancelInvoice(id); loadTableData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }

    private void doRecordPayment() {
        int row = tblInvoices.getSelectedRow();
        if (row < 0) return;
        long id = (long) invoiceModel.getValueAt(row, 0);
        try {
            Invoice inv = financeCtrl.getAllInvoices().stream().filter(i -> i.getInvoiceId() == id).findFirst().orElse(null);
            financeCtrl.recordPayment(inv, new BigDecimal(txtAmount.getText()), (PaymentMethod) cmbMethod.getSelectedItem(), txtRef.getText());
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            txtAmount.setText(""); txtRef.setText("");
            loadTableData(); loadPaymentsForSelectedInvoice();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InvoiceManagerFrame().setVisible(true));
    }
}
