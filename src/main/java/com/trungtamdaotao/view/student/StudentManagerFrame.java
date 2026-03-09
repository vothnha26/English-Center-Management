package com.trungtamdaotao.view.student;

import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.Gender;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StudentManagerFrame extends BaseManagerFrame {

    private final StudentController studentController;
    private JTable tblStudent;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtFullName, txtPhone, txtEmail, txtAddress, txtDob, txtSearch;
    private JComboBox<Gender> cmbGender;
    private JComboBox<Status> cmbStatus;
    
    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReload;

    public StudentManagerFrame() {
        super("Quản lý Học viên");
        this.studentController = new StudentController();
        loadTableData(); // Đảm bảo load dữ liệu sau khi controller được khởi tạo
    }

    @Override
    protected void initComponents() {
        // --- Toolbar (NORTH) ---
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlToolbar.setBackground(UIHelper.PRIMARY_COLOR);
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setForeground(Color.WHITE);
        lblSearch.setFont(UIHelper.BOLD_FONT);
        pnlToolbar.add(lblSearch);
        
        txtSearch = new JTextField(25);
        pnlToolbar.add(txtSearch);
        
        btnSearch = UIHelper.createStandardButton("Tìm", Color.WHITE, "🔍");
        btnSearch.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnSearch);
        
        btnReload = UIHelper.createStandardButton("Tải lại", Color.WHITE, "⟳");
        btnReload.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnReload);
        
        add(pnlToolbar, BorderLayout.NORTH);

        // --- Form (WEST) ---
        JPanel pnlForm = UIHelper.createFormPanel("Thông tin học viên");
        pnlForm.setPreferredSize(new Dimension(400, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        // Fields
        addFormField(pnlForm, "Họ tên:", txtFullName = new JTextField(), gbc, row++);
        addFormField(pnlForm, "Điện thoại:", txtPhone = new JTextField(), gbc, row++);
        addFormField(pnlForm, "Email:", txtEmail = new JTextField(), gbc, row++);
        addFormField(pnlForm, "Địa chỉ:", txtAddress = new JTextField(), gbc, row++);
        addFormField(pnlForm, "Ngày sinh:", txtDob = new JTextField(), gbc, row++);
        
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        cmbGender = new JComboBox<>(Gender.values());
        pnlForm.add(cmbGender, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(Status.values());
        pnlForm.add(cmbStatus, gbc);
        row++;

        // Buttons Panel
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlButtons.setOpaque(false);
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        btnAdd = UIHelper.createStandardButton("Thêm", UIHelper.SUCCESS_COLOR, "✚");
        btnUpdate = UIHelper.createStandardButton("Sửa", UIHelper.WARNING_COLOR, "✎");
        btnDelete = UIHelper.createStandardButton("Xóa", UIHelper.DANGER_COLOR, "✘");
        btnClear = UIHelper.createStandardButton("Mới", UIHelper.PRIMARY_COLOR, "⟲");
        
        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnClear);
        
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.WEST);

        // --- Table (CENTER) ---
        String[] columns = {"ID", "Họ tên", "Điện thoại", "Email", "Giới tính", "Ngày sinh", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblStudent = new JTable(tableModel);
        setupTable(tblStudent);
        add(new JScrollPane(tblStudent), BorderLayout.CENTER);
    }

    private void addFormField(JPanel p, String label, JTextField tf, GridBagConstraints gbc, int r) {
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 1;
        p.add(createFieldLabel(label), gbc);
        gbc.gridx = 1;
        p.add(tf, gbc);
    }

    @Override
    protected void handleEvents() {
        btnReload.addActionListener(e -> loadTableData());
        btnSearch.addActionListener(e -> searchStudents());
        btnClear.addActionListener(e -> clearForm());
        
        btnAdd.addActionListener(e -> {
            try {
                studentController.addStudent(
                    txtFullName.getText(), txtPhone.getText(), txtEmail.getText(),
                    txtAddress.getText(), LocalDate.parse(txtDob.getText())
                );
                loadTableData();
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        tblStudent.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillForm();
        });
    }

    @Override
    protected void loadTableData() {
        if (studentController == null) return;
        List<Student> list = studentController.getAllStudents();
        renderTable(list);
    }

    private void renderTable(List<Student> list) {
        tableModel.setRowCount(0);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                s.getStudent_id(), s.getFullName(), s.getPhone(), s.getEmail(),
                s.getGender(), s.getDateOfBirth(), s.getStatus()
            });
        }
    }

    private void searchStudents() {
        String kw = txtSearch.getText();
        renderTable(studentController.searchStudents(kw));
    }

    private void fillForm() {
        int row = tblStudent.getSelectedRow();
        if (row >= 0) {
            txtFullName.setText(tableModel.getValueAt(row, 1).toString());
            txtPhone.setText(tableModel.getValueAt(row, 2).toString());
            txtEmail.setText(tableModel.getValueAt(row, 3).toString());
            cmbGender.setSelectedItem(tableModel.getValueAt(row, 4));
            txtDob.setText(tableModel.getValueAt(row, 5).toString());
            cmbStatus.setSelectedItem(tableModel.getValueAt(row, 6));
        }
    }

    private void clearForm() {
        txtFullName.setText(""); txtPhone.setText(""); txtEmail.setText("");
        txtAddress.setText(""); txtDob.setText(""); txtSearch.setText("");
        cmbGender.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
        tblStudent.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagerFrame().setVisible(true));
    }
}
