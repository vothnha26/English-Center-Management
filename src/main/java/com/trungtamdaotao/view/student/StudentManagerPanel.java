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
 * Quản lý học viên - Tiếng Việt có dấu & Icon trực quan.
 */
public class StudentManagerPanel extends BaseManagerPanel {

    private final StudentController studentController;
    private JTable tblStudent;
    private DefaultTableModel tableModel;
    private JTextField txtFullName, txtPhone, txtEmail, txtAddress, txtDob, txtSearch;
    private JComboBox<Gender> cmbGender;
    private JComboBox<Status> cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReload;

    public StudentManagerPanel() {
        super();
        this.studentController = new StudentController();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(750);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);

        // --- MASTER SIDE (LEFT) ---
        JPanel pnlMaster = new JPanel(new BorderLayout(0, 10));
        pnlMaster.setOpaque(false);
        pnlMaster.setBorder(new EmptyBorder(0, 0, 0, 10));

        JPanel pnlSearch = new JPanel(new MigLayout("insets 0", "[grow]5[]5[]"));
        pnlSearch.setOpaque(false);
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm học viên...");
        pnlSearch.add(txtSearch, "grow, height 32");
        
        btnSearch = UIHelper.createStandardButton("Tìm", UIHelper.PRIMARY_COLOR, "\uD83D\uDD0D");
        btnReload = UIHelper.createStandardButton("Tải lại", UIHelper.ACCENT_COLOR, "\u21BB");
        pnlSearch.add(btnSearch, "height 32");
        pnlSearch.add(btnReload, "height 32");
        pnlMaster.add(pnlSearch, BorderLayout.NORTH);

        String[] columns = {"ID", "Họ tên", "Điện thoại", "Email", "Giới tính", "Ngày sinh", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblStudent = new JTable(tableModel);
        setupTable(tblStudent);
        pnlMaster.add(new JScrollPane(tblStudent), BorderLayout.CENTER);
        splitPane.setLeftComponent(pnlMaster);

        // --- DETAIL SIDE (RIGHT) ---
        JPanel pnlDetail = new JPanel(new BorderLayout());
        pnlDetail.setBackground(Color.WHITE);
        pnlDetail.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JLabel lblDetailTitle = new JLabel("HỒ SƠ HỌC VIÊN");
        lblDetailTitle.setFont(UIHelper.TITLE_FONT);
        lblDetailTitle.setForeground(UIHelper.PRIMARY_COLOR);
        lblDetailTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblDetailTitle.setBorder(new EmptyBorder(20, 0, 15, 0));
        pnlDetail.add(lblDetailTitle, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new MigLayout("wrap 2, inset 15, fillx", "[][grow, fill]", "[]10[]10[]10[]10[]10[]10[]10[]20[]"));
        pnlForm.setOpaque(false);

        pnlForm.add(createFieldLabel("Họ tên:")); pnlForm.add(txtFullName = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Điện thoại:")); pnlForm.add(txtPhone = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Email:")); pnlForm.add(txtEmail = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Địa chỉ:")); pnlForm.add(txtAddress = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Ngày sinh:")); pnlForm.add(txtDob = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Giới tính:")); pnlForm.add(cmbGender = new JComboBox<>(Gender.values()), "height 32");
        pnlForm.add(createFieldLabel("Trạng thái:")); pnlForm.add(cmbStatus = new JComboBox<>(Status.values()), "height 32");

        // Action Buttons Row (Gọn gàng)
        JPanel pnlActions = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlActions.setOpaque(false);
        btnAdd = UIHelper.createStandardButton("Thêm mới", UIHelper.SUCCESS_COLOR, "\u2795");
        btnUpdate = UIHelper.createStandardButton("Cập nhật", UIHelper.WARNING_COLOR, "\u270E");
        btnDelete = UIHelper.createStandardButton("Xóa hồ sơ", UIHelper.DANGER_COLOR, "\u2718");
        btnClear = UIHelper.createStandardButton("Làm mới", UIHelper.ACCENT_COLOR, "\u21B6");
        
        pnlActions.add(btnAdd); pnlActions.add(btnUpdate); pnlActions.add(btnDelete); pnlActions.add(btnClear);
        pnlForm.add(pnlActions, "span 2, growx");

        pnlDetail.add(pnlForm, BorderLayout.CENTER);
        splitPane.setRightComponent(pnlDetail);

        add(splitPane, BorderLayout.CENTER);
    }

    @Override protected void handleEvents() {
        btnReload.addActionListener(e -> loadTableData());
        btnSearch.addActionListener(e -> searchStudents());
        btnClear.addActionListener(e -> clearForm());
        tblStudent.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) fillForm(); });
    }

    @Override protected void loadTableData() { if (studentController != null) renderTable(studentController.getAllStudents()); }

    private void renderTable(List<Student> list) {
        tableModel.setRowCount(0);
        for (Student s : list) tableModel.addRow(new Object[]{ s.getStudent_id(), s.getFullName(), s.getPhone(), s.getEmail(), s.getGender(), s.getDateOfBirth(), s.getStatus() });
    }

    private void searchStudents() { renderTable(studentController.searchStudents(txtSearch.getText())); }

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
        txtFullName.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtAddress.setText(""); txtDob.setText("");
        cmbGender.setSelectedIndex(0); cmbStatus.setSelectedIndex(0); tblStudent.clearSelection();
    }
}
