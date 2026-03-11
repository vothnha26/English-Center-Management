package com.trungtamdaotao.view.student;

import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.Gender;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Quản lý học viên nâng cao - Tích hợp Sub-tabs Hồ sơ, Điểm số và Học phí.
 */
public class StudentManagerPanel extends BaseManagerPanel {

    private final StudentController studentController;
    private JTable tblStudent;
    private DefaultTableModel tableModel;
    
    // Form fields (Tab 1)
    private JTextField txtFullName, txtPhone, txtEmail, txtAddress, txtDob, txtSearch;
    private JComboBox<Gender> cmbGender;
    private JComboBox<Status> cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReload;
    
    // Tables for Tab 2 & 3
    private JTable tblGrades, tblPayments;
    private DefaultTableModel gradeModel, paymentModel;

    public StudentManagerPanel() {
        super();
        this.studentController = new StudentController();
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
        detailTabs.addTab("ĐIỂM SỐ", createGradesTab());
        detailTabs.addTab("HỌC PHÍ", createPaymentsTab());

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
        pnlForm.add(createFieldLabel("Địa chỉ:")); pnlForm.add(txtAddress = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Ngày sinh (yyyy-MM-dd):")); pnlForm.add(txtDob = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Giới tính:")); pnlForm.add(cmbGender = new JComboBox<>(Gender.values()), "height 32");
        pnlForm.add(createFieldLabel("Trạng thái:")); pnlForm.add(cmbStatus = new JComboBox<>(Status.values()), "height 32");

        JPanel pnlActions = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlActions.setOpaque(false);
        btnAdd = UIHelper.createStandardButton("Thêm", UIHelper.SUCCESS_COLOR, "\u2795");
        btnUpdate = UIHelper.createStandardButton("Sửa", UIHelper.WARNING_COLOR, "\u270E");
        btnDelete = UIHelper.createStandardButton("Xoa", UIHelper.DANGER_COLOR, "\u2718");
        btnClear = UIHelper.createStandardButton("Mới", UIHelper.ACCENT_COLOR, "\u21B6");
        pnlActions.add(btnAdd);
        pnlActions.add(btnUpdate);
        pnlActions.add(btnDelete);
        pnlActions.add(btnClear);
        pnlForm.add(pnlActions, "span 2, growx");

        p.add(pnlForm, BorderLayout.NORTH);
        return p;
    }

    private JPanel createGradesTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("KẾT QUẢ HỌC TẬP");
        lblTitle.setFont(UIHelper.BOLD_FONT); lblTitle.setForeground(UIHelper.SECONDARY_COLOR);
        p.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Khóa học", "Lớp học", "Điểm", "Xếp loại"};
        gradeModel = new DefaultTableModel(cols, 0);
        tblGrades = new JTable(gradeModel);
        setupTable(tblGrades);
        p.add(new JScrollPane(tblGrades), BorderLayout.CENTER);
        
        return p;
    }

    private JPanel createPaymentsTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("LỊCH SỬ THANH TOÁN");
        lblTitle.setFont(UIHelper.BOLD_FONT); lblTitle.setForeground(UIHelper.SUCCESS_COLOR);
        p.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Ngày đóng", "Số tiền", "Hình thức", "Trạng thái"};
        paymentModel = new DefaultTableModel(cols, 0);
        tblPayments = new JTable(paymentModel);
        setupTable(tblPayments);
        p.add(new JScrollPane(tblPayments), BorderLayout.CENTER);

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

                studentController.addStudent(fullName, phone, email, address, dob);
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
        txtAddress.setText("");
        txtDob.setText("");
        cmbGender.setSelectedIndex(0);
        cmbStatus.setSelectedItem(Status.Active);
        tblStudent.clearSelection();
        gradeModel.setRowCount(0);
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
                
                // Cập nhật các Tab chi tiết (Sẽ triển khai Service tương ứng)
                gradeModel.setRowCount(0);
                paymentModel.setRowCount(0);
            }
        }
    }
}
