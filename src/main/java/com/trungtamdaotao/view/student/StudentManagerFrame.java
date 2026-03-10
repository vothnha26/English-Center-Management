package com.trungtamdaotao.view.student;

import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.Gender;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Quản lý học viên với mô hình Master-Detail hiện đại.
 */
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
        super("QUẢN LÝ HỌC VIÊN");
        this.studentController = new StudentController();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        // Layout chính Master-Detail
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);
        splitPane.setDividerSize(10);
        splitPane.setBackground(UIHelper.BACKGROUND_COLOR);

        // --- MASTER SIDE (LEFT) ---
        JPanel pnlMaster = new JPanel(new BorderLayout(0, 15));
        pnlMaster.setOpaque(false);
        pnlMaster.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Search Bar Row
        JPanel pnlSearch = new JPanel(new MigLayout("insets 0", "[grow]10[]10[]"));
        pnlSearch.setOpaque(false);
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm theo tên, số điện thoại hoặc email...");
        pnlSearch.add(txtSearch, "grow, height 35");
        
        btnSearch = UIHelper.createStandardButton("Tìm", UIHelper.PRIMARY_COLOR, "🔍");
        btnReload = UIHelper.createStandardButton("Tải lại", UIHelper.ACCENT_COLOR, "⟳");
        pnlSearch.add(btnSearch, "height 35");
        pnlSearch.add(btnReload, "height 35");
        
        pnlMaster.add(pnlSearch, BorderLayout.NORTH);

        // Table List
        String[] columns = {"ID", "Họ tên", "Điện thoại", "Email", "Giới tính", "Ngày sinh", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblStudent = new JTable(tableModel);
        setupTable(tblStudent);
        pnlMaster.add(new JScrollPane(tblStudent), BorderLayout.CENTER);

        splitPane.setLeftComponent(pnlMaster);

        // --- DETAIL SIDE (RIGHT) ---
        JPanel pnlDetail = new JPanel(new BorderLayout());
        pnlDetail.setBackground(Color.WHITE);
        pnlDetail.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(220, 220, 220)));

        // Detail Header
        JLabel lblDetailTitle = new JLabel("HỒ SƠ HỌC VIÊN");
        lblDetailTitle.setFont(UIHelper.TITLE_FONT);
        lblDetailTitle.setForeground(UIHelper.PRIMARY_COLOR);
        lblDetailTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblDetailTitle.setBorder(new EmptyBorder(30, 0, 20, 0));
        pnlDetail.add(lblDetailTitle, BorderLayout.NORTH);

        // Detail Form
        JPanel pnlForm = new JPanel(new MigLayout("wrap 2, inset 25, fillx", "[][grow, fill]", "[]15[]15[]15[]15[]15[]15[]25[]"));
        pnlForm.setOpaque(false);

        pnlForm.add(createFieldLabel("Họ tên:"));
        pnlForm.add(txtFullName = new JTextField(), "height 35");
        
        pnlForm.add(createFieldLabel("Điện thoại:"));
        pnlForm.add(txtPhone = new JTextField(), "height 35");
        
        pnlForm.add(createFieldLabel("Email:"));
        pnlForm.add(txtEmail = new JTextField(), "height 35");
        
        pnlForm.add(createFieldLabel("Địa chỉ:"));
        pnlForm.add(txtAddress = new JTextField(), "height 35");
        
        pnlForm.add(createFieldLabel("Ngày sinh (yyyy-mm-dd):"));
        pnlForm.add(txtDob = new JTextField(), "height 35");
        
        pnlForm.add(createFieldLabel("Giới tính:"));
        pnlForm.add(cmbGender = new JComboBox<>(Gender.values()), "height 35");
        
        pnlForm.add(createFieldLabel("Trạng thái:"));
        pnlForm.add(cmbStatus = new JComboBox<>(Status.values()), "height 35");

        // Action Buttons
        JPanel pnlActions = new JPanel(new MigLayout("insets 0, fillx", "[grow][grow][grow]", "[]"));
        pnlActions.setOpaque(false);
        
        btnAdd = UIHelper.createStandardButton("Thêm", UIHelper.SUCCESS_COLOR, "✚");
        btnUpdate = UIHelper.createStandardButton("Sửa", UIHelper.WARNING_COLOR, "✎");
        btnDelete = UIHelper.createStandardButton("Xóa", UIHelper.DANGER_COLOR, "✘");
        btnClear = UIHelper.createStandardButton("Làm mới", UIHelper.ACCENT_COLOR, "⟲");

        pnlActions.add(btnAdd, "grow, height 40");
        pnlActions.add(btnUpdate, "grow, height 40");
        pnlActions.add(btnDelete, "grow, height 40");
        
        pnlForm.add(pnlActions, "span 2, growx");
        pnlForm.add(btnClear, "span 2, growx, height 40");

        pnlDetail.add(pnlForm, BorderLayout.CENTER);

        splitPane.setRightComponent(pnlDetail);

        add(splitPane, BorderLayout.CENTER);
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
                JOptionPane.showMessageDialog(this, "Thêm học viên thành công!");
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
        // Apply FlatLaf for testing
        com.formdev.flatlaf.FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new StudentManagerFrame().setVisible(true));
    }
}
