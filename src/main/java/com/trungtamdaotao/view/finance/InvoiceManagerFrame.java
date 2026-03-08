package com.trungtamdaotao.view.finance;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.trungtamdaotao.controller.finance.FinanceController;
import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.InvoiceStatus;
import com.trungtamdaotao.model.entity.enums.PaymentMethod;
import com.trungtamdaotao.model.entity.finance.Invoice;
import com.trungtamdaotao.model.entity.finance.Payment;

/**
 * Màn hình Quản lý Hóa đơn & Thanh toán.
 * Bố cục:
 *  - NORTH  : bộ lọc học viên + nút tạo hóa đơn mới
 *  - CENTER : bảng hóa đơn (trái) | bảng chi tiết thanh toán (phải)
 *  - SOUTH  : form ghi nhận thanh toán
 */
public class InvoiceManagerFrame extends JFrame {

    private final FinanceController financeCtrl;
    private final StudentController studentCtrl;

    // Bảng hóa đơn
    private JTable tblInvoices;
    private DefaultTableModel invoiceModel;

    // Bảng thanh toán của hóa đơn đang chọn
    private JTable tblPayments;
    private DefaultTableModel paymentModel;

    // Filter
    private JComboBox<Student> cmbStudent;
    private JComboBox<String>  cmbStatus;

    // Form thanh toán
    private JTextField txtAmount, txtRef;
    private JComboBox<PaymentMethod> cmbMethod;

    private static final String[] INV_COLS  = {"ID", "Học viên", "Số tiền", "Ngày tạo", "Trạng thái", "Ghi chú"};
    private static final String[] PAY_COLS  = {"ID", "Số tiền", "Phương thức", "Ngày TT", "Mã TK"};

    public InvoiceManagerFrame() {
        this.financeCtrl = new FinanceController();
        this.studentCtrl = new StudentController();
        initUI();
        loadStudentCombo();
        loadInvoices(financeCtrl.getAllInvoices());
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void initUI() {
        setTitle("Quản lý Hóa đơn & Thanh toán");
        setSize(1100, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(buildFilterPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildPaymentForm(),  BorderLayout.SOUTH);
    }

    // ── Bộ lọc + tạo hóa đơn ────────────────────────────────────────────────
    private JPanel buildFilterPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBorder(BorderFactory.createTitledBorder("Lọc & Tạo hóa đơn"));

        cmbStudent = new JComboBox<>();
        cmbStatus  = new JComboBox<>(new String[]{"Tất cả", "Issued", "Paid", "Cancelled"});

        JButton btnFilter       = new JButton("Lọc");
        JButton btnNewInvoice   = new JButton("➕ Tạo hóa đơn");
        JButton btnCancelInv    = new JButton("❌ Huỷ hóa đơn");
        JButton btnAll          = new JButton("Tải lại");

        btnFilter.addActionListener(e     -> doFilter());
        btnAll.addActionListener(e        -> loadInvoices(financeCtrl.getAllInvoices()));
        btnNewInvoice.addActionListener(e -> doCreateInvoice());
        btnCancelInv.addActionListener(e  -> doCancelInvoice());

        p.add(new JLabel("Học viên:")); p.add(cmbStudent);
        p.add(new JLabel("Trạng thái:")); p.add(cmbStatus);
        p.add(btnFilter); p.add(btnAll);
        p.add(Box.createHorizontalStrut(20));
        p.add(btnNewInvoice); p.add(btnCancelInv);
        return p;
    }

    // ── Bảng hóa đơn (trái) + bảng thanh toán (phải) ────────────────────────
    private JSplitPane buildCenterPanel() {
        invoiceModel = new DefaultTableModel(INV_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblInvoices = new JTable(invoiceModel);
        tblInvoices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblInvoices.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadPaymentsForSelectedInvoice();
        });
        tblInvoices.getColumnModel().getColumn(0).setMaxWidth(50);

        paymentModel = new DefaultTableModel(PAY_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPayments = new JTable(paymentModel);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));
        leftPanel.add(new JScrollPane(tblInvoices));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết thanh toán"));
        rightPanel.add(new JScrollPane(tblPayments));

        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
    }

    // ── Form ghi nhận thanh toán ─────────────────────────────────────────────
    private JPanel buildPaymentForm() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBorder(BorderFactory.createTitledBorder("Ghi nhận thanh toán cho hóa đơn đang chọn"));

        txtAmount  = new JTextField(12);
        txtRef     = new JTextField(14);
        cmbMethod  = new JComboBox<>(PaymentMethod.values());
        JButton btnPay = new JButton("💰 Xác nhận thanh toán");

        btnPay.addActionListener(e -> doRecordPayment());

        p.add(new JLabel("Số tiền:")); p.add(txtAmount);
        p.add(new JLabel("Phương thức:")); p.add(cmbMethod);
        p.add(new JLabel("Mã tham chiếu:")); p.add(txtRef);
        p.add(btnPay);
        return p;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Nạp ComboBox học viên
    private void loadStudentCombo() {
        cmbStudent.removeAllItems();
        cmbStudent.addItem(null); // hiển thị "Tất cả"
        for (Student s : studentCtrl.getAllStudents()) cmbStudent.addItem(s);
    }

    // Nạp bảng hóa đơn
    private void loadInvoices(List<Invoice> list) {
        invoiceModel.setRowCount(0);
        for (Invoice inv : list) {
            invoiceModel.addRow(new Object[]{
                inv.getInvoiceId(),
                inv.getStudent().getFullName(),
                inv.getTotalAmount(),
                inv.getIssueDate(),
                inv.getStatus(),
                inv.getNote()
            });
        }
    }

    // Nạp thanh toán của hóa đơn được chọn
    private void loadPaymentsForSelectedInvoice() {
        paymentModel.setRowCount(0);
        int row = tblInvoices.getSelectedRow();
        if (row < 0) return;
        long invoiceId = (long) invoiceModel.getValueAt(row, 0);
        List<Payment> payments = financeCtrl.getPaymentsByInvoice(invoiceId);
        for (Payment p : payments) {
            paymentModel.addRow(new Object[]{
                p.getPaymentId(),
                p.getAmount(),
                p.getPaymentMethod(),
                p.getPaymentDate(),
                p.getReferenceCode()
            });
        }
    }

    // Lọc hóa đơn theo học viên + trạng thái
    private void doFilter() {
        Student selectedStudent = (Student) cmbStudent.getSelectedItem();
        String  statusStr       = (String) cmbStatus.getSelectedItem();

        List<Invoice> allInvoices = (selectedStudent != null)
                ? financeCtrl.getInvoicesByStudent(selectedStudent.getStudentId())
                : financeCtrl.getAllInvoices();

        if (!"Tất cả".equals(statusStr)) {
            InvoiceStatus filterStatus = InvoiceStatus.valueOf(statusStr);
            allInvoices = allInvoices.stream()
                    .filter(i -> i.getStatus() == filterStatus)
                    .toList();
        }
        loadInvoices(allInvoices);
    }

    // Tạo hóa đơn mới cho học viên đang chọn trong combo
    private void doCreateInvoice() {
        Student student = (Student) cmbStudent.getSelectedItem();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên để tạo hóa đơn.");
            return;
        }
        String amtStr = JOptionPane.showInputDialog(this, "Nhập số tiền học phí:");
        if (amtStr == null || amtStr.isBlank()) return;
        String note = JOptionPane.showInputDialog(this, "Ghi chú (có thể bỏ trống):");
        try {
            BigDecimal amount = new BigDecimal(amtStr.replace(",", "").replace(".", "").trim());
            financeCtrl.createInvoice(student, amount, note);
            JOptionPane.showMessageDialog(this, "Đã tạo hóa đơn thành công!");
            loadInvoices(financeCtrl.getAllInvoices());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Huỷ hóa đơn đang chọn
    private void doCancelInvoice() {
        int row = tblInvoices.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần huỷ."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Huỷ hóa đơn này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        int invoiceId = (int) ((long) invoiceModel.getValueAt(row, 0));
        try {
            financeCtrl.cancelInvoice(invoiceId);
            JOptionPane.showMessageDialog(this, "Đã huỷ hóa đơn.");
            loadInvoices(financeCtrl.getAllInvoices());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Ghi nhận thanh toán
    private void doRecordPayment() {
        int row = tblInvoices.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần thanh toán."); return; }

        long invoiceId = (long) invoiceModel.getValueAt(row, 0);
        // Tải lại invoice đầy đủ từ controller
        List<Invoice> allInv = financeCtrl.getAllInvoices();
        Invoice inv = allInv.stream().filter(i -> i.getInvoiceId() == invoiceId).findFirst().orElse(null);
        if (inv == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn."); return; }
        if (inv.getStatus() == InvoiceStatus.Paid || inv.getStatus() == InvoiceStatus.Cancelled) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã được thanh toán hoặc đã bị huỷ.");
            return;
        }
        try {
            BigDecimal amount = new BigDecimal(txtAmount.getText().trim());
            PaymentMethod method = (PaymentMethod) cmbMethod.getSelectedItem();
            String ref = txtRef.getText().trim();
            financeCtrl.recordPayment(inv, amount, method, ref);
            JOptionPane.showMessageDialog(this, "Ghi nhận thanh toán thành công!");
            txtAmount.setText(""); txtRef.setText("");
            loadInvoices(financeCtrl.getAllInvoices());
            loadPaymentsForSelectedInvoice();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InvoiceManagerFrame().setVisible(true));
    }
}
