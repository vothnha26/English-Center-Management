package com.trungtamdaotao.view.student;

import com.trungtamdaotao.controller.finance.FinanceController;
import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.academic.Enrollment;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.Gender;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.model.entity.finance.Payment;
import com.trungtamdaotao.model.entity.enums.PaymentMethod;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Quản lý học viên nâng cao - Tích hợp Sub-tabs Hồ sơ, Điểm số và Học phí.
 */
public class StudentManagerPanel extends BaseManagerPanel {

    private final StudentController studentController;
    private final FinanceController financeController;
    private JTable tblStudent;
    private DefaultTableModel tableModel;
    
    // Form fields (Tab 1)
    private JTextField txtFullName, txtPhone, txtEmail, txtAddress, txtDob, txtSearch;
    private JPasswordField txtPassword;
    private JComboBox<Gender> cmbGender;
    private JComboBox<Status> cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReload, btnEnroll;
    
    // Tables for Tab 2 & 3
    private JTable tblEnrollments, tblPayments;
    private DefaultTableModel enrollmentModel, paymentModel;
    private JButton btnCancelEnrollment, btnCreateInvoice, btnRecordPayment;

    public StudentManagerPanel() {
        super();
        this.studentController = new StudentController();
        this.financeController = new FinanceController();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);

        // --- MASTER SIDE (LEFT) ---
        JPanel pnlMaster = new JPanel(new BorderLayout(0, 10));
        pnlMaster.setOpaque(false);
        pnlMaster.setBorder(new EmptyBorder(0, 0, 0, 10));

        JPanel pnlSearch = new JPanel(new MigLayout("insets 0", "[grow]5[]5[]"));
        pnlSearch.setOpaque(false);
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm học viên...");
        btnSearch = UIHelper.createStandardButton("Tìm", UIHelper.PRIMARY_COLOR, "\uD83D\uDD0D");
        btnReload = UIHelper.createStandardButton("Tải lại", UIHelper.ACCENT_COLOR, "\u21BB");
        pnlSearch.add(txtSearch, "grow, height 32");
        pnlSearch.add(btnSearch, "height 32");
        pnlSearch.add(btnReload, "height 32");
        pnlMaster.add(pnlSearch, BorderLayout.NORTH);

        String[] columns = {"ID", "Họ tên", "Điện thoại", "Email", "Giới tính", "Ngày sinh", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblStudent = new JTable(tableModel);
        setupTable(tblStudent);
        pnlMaster.add(new JScrollPane(tblStudent), BorderLayout.CENTER);
        splitPane.setLeftComponent(pnlMaster);

        // --- DETAIL SIDE (RIGHT) WITH TABS ---
        JTabbedPane detailTabs = new JTabbedPane();
        detailTabs.setFont(UIHelper.BOLD_FONT);
        detailTabs.setBackground(Color.WHITE);

        detailTabs.addTab("HỒ SƠ", createProfileTab());
        detailTabs.addTab("CHI TIẾT GHI DANH", createEnrollmentsTab());
        detailTabs.addTab("LỊCH SỬ THANH TOÁN", createPaymentsTab());

        splitPane.setRightComponent(detailTabs);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createProfileTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        JPanel pnlForm = new JPanel(new MigLayout("wrap 2, inset 20, fillx", "[][grow, fill]", "[]15[]10[]10[]10[]10[]10[]10[]10[]25[]"));
        pnlForm.setOpaque(false);

        JLabel lblTitle = new JLabel("THÔNG TIN CÁ NHÂN");
        lblTitle.setFont(UIHelper.TITLE_FONT); lblTitle.setForeground(UIHelper.PRIMARY_COLOR);
        pnlForm.add(lblTitle, "span 2, center, gapy 0 15");

        pnlForm.add(createFieldLabel("Họ tên:")); pnlForm.add(txtFullName = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Điện thoại:")); pnlForm.add(txtPhone = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Email:")); pnlForm.add(txtEmail = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Mật khẩu (tùy chọn):")); pnlForm.add(txtPassword = new JPasswordField(), "height 32");
        pnlForm.add(createFieldLabel("Địa chỉ:")); pnlForm.add(txtAddress = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Ngày sinh (yyyy-MM-dd):")); pnlForm.add(txtDob = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Giới tính:")); pnlForm.add(cmbGender = new JComboBox<>(Gender.values()), "height 32");
        pnlForm.add(createFieldLabel("Trạng thái:")); pnlForm.add(cmbStatus = new JComboBox<>(Status.values()), "height 32");

        JPanel pnlActions = new JPanel(new GridLayout(2, 3, 5, 5));
        pnlActions.setOpaque(false);
        btnAdd = UIHelper.createStandardButton("Thêm", UIHelper.SUCCESS_COLOR, "➕");
        btnUpdate = UIHelper.createStandardButton("Sửa", UIHelper.WARNING_COLOR, "📝");
        btnDelete = UIHelper.createStandardButton("Xóa", UIHelper.DANGER_COLOR, "🗑");
        btnEnroll = UIHelper.createStandardButton("Ghi danh", UIHelper.PRIMARY_COLOR, "📋");
        btnClear = UIHelper.createStandardButton("Mới", UIHelper.ACCENT_COLOR, "🔄");
        JButton btnPlaceholder = new JButton(); btnPlaceholder.setEnabled(false);
        pnlActions.add(btnAdd);
        pnlActions.add(btnUpdate);
        pnlActions.add(btnDelete);
        pnlActions.add(btnEnroll);
        pnlActions.add(btnClear);
        pnlActions.add(btnPlaceholder);
        pnlForm.add(pnlActions, "span 2, growx");

        p.add(pnlForm, BorderLayout.NORTH);
        return p;
    }

    private JPanel createEnrollmentsTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("DANH SÁCH GHI DANH");
        lblTitle.setFont(UIHelper.BOLD_FONT); lblTitle.setForeground(UIHelper.PRIMARY_COLOR);
        p.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Lớp học", "Ngày ghi danh", "Trạng thái"};
        enrollmentModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEnrollments = new JTable(enrollmentModel);
        setupTable(tblEnrollments);
        
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlBottom.setBackground(Color.WHITE);
        btnCancelEnrollment = UIHelper.createStandardButton("❌ Huỷ ghi danh", Color.RED, "");
        btnCancelEnrollment.addActionListener(e -> doCancelEnrollment());
        btnCreateInvoice = UIHelper.createStandardButton("📄 Tạo hóa đơn", UIHelper.PRIMARY_COLOR, "");
        btnCreateInvoice.addActionListener(e -> doCreateInvoiceFromEnrollment());
        pnlBottom.add(btnCreateInvoice);
        pnlBottom.add(btnCancelEnrollment);
        
        p.add(new JScrollPane(tblEnrollments), BorderLayout.CENTER);
        p.add(pnlBottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createPaymentsTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("LỊCH SỬ THANH TOÁN");
        lblTitle.setFont(UIHelper.BOLD_FONT); lblTitle.setForeground(UIHelper.SUCCESS_COLOR);
        p.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Ngày phát hành", "Tổng tiền", "Trạng thái", "Ghi chú"};
        paymentModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPayments = new JTable(paymentModel);
        setupTable(tblPayments);
        
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlBottom.setBackground(Color.WHITE);
        btnRecordPayment = UIHelper.createStandardButton("✅ Ghi nhận thanh toán", UIHelper.SUCCESS_COLOR, "");
        btnRecordPayment.addActionListener(e -> doRecordPayment());
        pnlBottom.add(btnRecordPayment);

        p.add(new JScrollPane(tblPayments), BorderLayout.CENTER);
        p.add(pnlBottom, BorderLayout.SOUTH);
        return p;
    }

    @Override
    protected void handleEvents() {
        btnSearch.addActionListener(e -> loadTableData());
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            loadTableData();
        });

        btnAdd.addActionListener(e -> {
            try {
                String fullName = txtFullName.getText().trim();
                String phone = txtPhone.getText().trim();
                String email = txtEmail.getText().trim();
                String address = txtAddress.getText().trim();
                String dobStr = txtDob.getText().trim();
                LocalDate dob = dobStr.isEmpty() ? null : LocalDate.parse(dobStr);

                String pwd = new String(txtPassword.getPassword()).trim();
                if (pwd.isEmpty()) {
                    studentController.addStudent(fullName, phone, email, address, dob);
                } else {
                    studentController.addStudent(fullName, phone, email, address, dob, pwd);
                }
                JOptionPane.showMessageDialog(this, "Thêm học viên thành công!");
                loadTableData();
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = tblStudent.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên cần sửa!");
                return;
            }
            try {
                Long id = (Long) tableModel.getValueAt(row, 0);
                Student s = studentController.getStudentById(id);
                s.setFullName(txtFullName.getText().trim());
                s.setPhone(txtPhone.getText().trim());
                s.setEmail(txtEmail.getText().trim());
                s.setAddress(txtAddress.getText().trim());
                String dobStr = txtDob.getText().trim();
                s.setDateOfBirth(dobStr.isEmpty() ? null : LocalDate.parse(dobStr));
                s.setGender((Gender) cmbGender.getSelectedItem());
                s.setStatus((Status) cmbStatus.getSelectedItem());

                studentController.updateStudent(s);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadTableData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = tblStudent.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa (vô hiệu hóa) học viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Long id = (Long) tableModel.getValueAt(row, 0);
                studentController.deleteStudent(id);
                loadTableData();
                clearForm();
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnEnroll.addActionListener(e -> {
            int row = tblStudent.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên!");
                return;
            }
            Long studentId = (Long) tableModel.getValueAt(row, 0);
            Student s = studentController.getStudentById(studentId);
            if (s != null) {
                new EnrollmentDialog(SwingUtilities.getWindowAncestor(this), s, studentController).setVisible(true);
                fillForm(); // Load lại data sau khi ghi danh
            }
        });

        tblStudent.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillForm();
        });
    }

    @Override protected void loadTableData() {
        if (studentController != null) {
            String keyword = txtSearch.getText().trim();
            List<Student> list;
            if (keyword.isEmpty()) {
                list = studentController.getAllStudents();
            } else {
                list = studentController.searchStudents(keyword);
            }
            
            tableModel.setRowCount(0);
            list.forEach(s -> tableModel.addRow(new Object[]{ 
                s.getStudent_id(), s.getFullName(), s.getPhone(), s.getEmail(), 
                s.getGender(), s.getDateOfBirth(), s.getStatus() 
            }));
        }
    }

    private void clearForm() {
        txtFullName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtAddress.setText("");
        txtDob.setText("");
        cmbGender.setSelectedIndex(0);
        cmbStatus.setSelectedItem(Status.Active);
        tblStudent.clearSelection();
        enrollmentModel.setRowCount(0);
        paymentModel.setRowCount(0);
    }

    private void fillForm() {
        int row = tblStudent.getSelectedRow();
        if (row >= 0) {
            Long id = (Long) tableModel.getValueAt(row, 0);
            Student s = studentController.getStudentById(id);
            if (s != null) {
                txtFullName.setText(s.getFullName());
                txtPhone.setText(s.getPhone());
                txtEmail.setText(s.getEmail());
                txtAddress.setText(s.getAddress());
                txtDob.setText(s.getDateOfBirth() != null ? s.getDateOfBirth().toString() : "");
                cmbGender.setSelectedItem(s.getGender());
                cmbStatus.setSelectedItem(s.getStatus());
                
                // Load Enrollments 
                enrollmentModel.setRowCount(0);
                List<Enrollment> enrollments = studentController.getEnrollmentsByStudent(id);
                if (enrollments != null) {
                    for (Enrollment e : enrollments) {
                        enrollmentModel.addRow(new Object[]{
                            e.getEnrollmentId(),
                            e.getClazz() != null ? e.getClazz().getClassName() : "",
                            e.getEnrollmentDate(),
                            e.getStatus()
                        });
                    }
                }
                
                // Load Invoices (as payment history)
                paymentModel.setRowCount(0);
                var invoices = financeController.getInvoicesByStudent(id);
                if (invoices != null) {
                    for (var inv : invoices) {
                        paymentModel.addRow(new Object[]{
                            inv.getInvoiceId(),
                            inv.getIssueDate(),
                            inv.getTotalAmount(),
                            inv.getStatus(),
                            inv.getNote()
                        });
                    }
                }
            }
        }
    }

    private void doCancelEnrollment() {
        int row = tblEnrollments.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ghi danh cần huỷ!");
            return;
        }
        Long enrollmentId = (Long) enrollmentModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Huỷ ghi danh này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                studentController.cancelEnrollment(enrollmentId);
                JOptionPane.showMessageDialog(this, "Đã huỷ ghi danh");
                fillForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doCreateInvoice() {
        // Legacy manual invoice creation (not used)
        JOptionPane.showMessageDialog(this, "Sử dụng chức năng 'Tạo hóa đơn' trên tab Ghi danh để tạo hoá đơn tự động.");
    }

    private void doCreateInvoiceFromEnrollment() {
        Long studentRow = (Long) tableModel.getValueAt(tblStudent.getSelectedRow(), 0);
        var enrollments = studentController.getEnrollmentsByStudent(studentRow);
        if (enrollments == null || enrollments.isEmpty()) { JOptionPane.showMessageDialog(this, "Học viên chưa có ghi danh nào."); return; }

        Enrollment en = null;
        int row = tblEnrollments.getSelectedRow();
        if (row >= 0) {
            Long enrollmentId = (Long) enrollmentModel.getValueAt(row, 0);
            en = enrollments.stream().filter(x -> x.getEnrollmentId().equals(enrollmentId)).findFirst().orElse(null);
        } else {
            // Auto-pick the latest enrollment by date
            en = enrollments.stream().sorted((a,b) -> b.getEnrollmentDate().compareTo(a.getEnrollmentDate())).findFirst().orElse(null);
        }
        if (en == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy ghi danh."); return; }
        var clazz = en.getClazz();
        if (clazz == null || clazz.getCourse() == null) { JOptionPane.showMessageDialog(this, "Lớp hoặc khóa không hợp lệ."); return; }
        try {
            BigDecimal fee = clazz.getCourse().getFee();
            financeController.createInvoice(studentController.getStudentById(studentRow), fee, "Học phí - " + clazz.getClassName());
            JOptionPane.showMessageDialog(this, "Đã tạo hóa đơn tự động (" + fee + ") cho học viên.");
            fillForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doRecordPayment() {
        int studentRow = tblStudent.getSelectedRow();
        if (studentRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên!");
            return;
        }
        int invRow = tblPayments.getSelectedRow();
        if (invRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để ghi nhận thanh toán!");
            return;
        }
        Long invoiceId = (Long) paymentModel.getValueAt(invRow, 0);
        Long studentId = (Long) tableModel.getValueAt(studentRow, 0);
        var invoices = financeController.getInvoicesByStudent(studentId);
        var inv = invoices.stream().filter(i -> i.getInvoiceId().equals(invoiceId)).findFirst().orElse(null);
        if (inv == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn."); return; }

        try {
            // Auto-pay remaining amount in one click using Cash
            var payments = financeController.getPaymentsByInvoice(inv.getInvoiceId());
            BigDecimal paid = payments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal remaining = inv.getTotalAmount().subtract(paid);
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Hóa đơn đã được thanh toán đầy đủ.");
                return;
            }
            financeController.recordPayment(inv, remaining, PaymentMethod.Cash, "Auto-pay");
            JOptionPane.showMessageDialog(this, "Ghi nhận thanh toán hoàn tất. Hóa đơn đã chuyển sang trạng thái Paid.");
            fillForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
