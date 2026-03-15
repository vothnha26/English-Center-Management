package com.trungtamdaotao.view.teacher;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.trungtamdaotao.controller.teacher.TeacherController;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.util.security.UserSession;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TeacherManagerFrame extends BaseManagerFrame {

    private final TeacherController controller;
    private JTable tblTeacher;
    private DefaultTableModel tableModel;

    private JTextField txtFullName, txtPhone, txtEmail, txtSpecialty, txtSearch;
    private DatePicker dpHireDate;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReload;

    public TeacherManagerFrame() {
        super("Quản lý Giáo viên", 
              new AccountRole[]{AccountRole.ADMIN, AccountRole.STAFF}, 
              new StaffRole[]{StaffRole.MANAGER});
        this.controller = new TeacherController();
        loadTableData();
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
        JPanel pnlForm = UIHelper.createFormPanel("Thông tin giáo viên");
        pnlForm.setPreferredSize(new Dimension(400, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFormField(pnlForm, "Họ tên (*):", txtFullName = new JTextField(), gbc, row++);
        addFormField(pnlForm, "Điện thoại (*):", txtPhone = new JTextField(), gbc, row++);
        addFormField(pnlForm, "Email:", txtEmail = new JTextField(), gbc, row++);
        addFormField(pnlForm, "Chuyên môn:", txtSpecialty = new JTextField(), gbc, row++);
        
        // DatePicker cho Ngày tuyển
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Ngày tuyển:"), gbc);
        gbc.gridx = 1;
        dpHireDate = UIHelper.createDatePicker();
        pnlForm.add(dpHireDate, gbc);
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
        if (UserSession.getPermissions().canDelete()) {
            pnlButtons.add(btnDelete);
        }
        pnlButtons.add(btnClear);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.WEST);

        // --- Table (CENTER) ---
        String[] columns = {"ID", "Họ tên", "Điện thoại", "Email", "Chuyên môn", "Ngày tuyển", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTeacher = new JTable(tableModel);
        setupTable(tblTeacher);
        add(new JScrollPane(tblTeacher), BorderLayout.CENTER);
    }

    private void addFormField(JPanel p, String label, JTextField tf, GridBagConstraints gbc, int r) {
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 1;
        p.add(createFieldLabel(label), gbc);
        gbc.gridx = 1;
        p.add(tf, gbc);
    }

    @Override
    protected void handleEvents() {
        btnSearch.addActionListener(e -> doSearch());
        btnReload.addActionListener(e -> loadTableData());
        btnAdd.addActionListener(e -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        if (UserSession.getPermissions().canDelete()) {
            btnDelete.addActionListener(e -> doDelete());
        }
        btnClear.addActionListener(e -> clearForm());

        tblTeacher.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });
    }

    @Override
    protected void loadTableData() {
        if (controller != null) renderTable(controller.getAllTeachers());
    }

    private void renderTable(List<Teacher> list) {
        tableModel.setRowCount(0);
        for (Teacher t : list) {
            tableModel.addRow(new Object[]{
                t.getTeacher_id(), t.getFullName(), t.getPhone(), t.getEmail(),
                t.getSpecialty(), t.getHireDate(), t.getStatus()
            });
        }
    }

    private void doSearch() {
        renderTable(controller.searchTeachers(txtSearch.getText()));
    }

    private void populateForm() {
        int row = tblTeacher.getSelectedRow();
        if (row < 0) return;
        txtFullName.setText(tableModel.getValueAt(row, 1).toString());
        txtPhone.setText(tableModel.getValueAt(row, 2).toString());
        txtEmail.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        txtSpecialty.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
        dpHireDate.setDate((LocalDate) tableModel.getValueAt(row, 5));
    }

    private void doAdd() {
        try {
            controller.addTeacher(txtFullName.getText(), txtPhone.getText(), txtEmail.getText(), txtSpecialty.getText(), dpHireDate.getDate());
            JOptionPane.showMessageDialog(this, "Thêm giáo viên thành công!");
            clearForm();
            loadTableData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void doUpdate() {
        int row = tblTeacher.getSelectedRow();
        if (row < 0) return;
        try {
            Long id = (Long) tableModel.getValueAt(row, 0);
            Teacher t = controller.getTeacherById(id);
            t.setFullName(txtFullName.getText());
            t.setPhone(txtPhone.getText());
            t.setEmail(txtEmail.getText());
            t.setSpecialty(txtSpecialty.getText());
            t.setHireDate(dpHireDate.getDate());
            controller.updateTeacher(t);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            clearForm();
            loadTableData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void doDelete() {
        int row = tblTeacher.getSelectedRow();
        if (row < 0) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Vô hiệu hóa giáo viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Long id = (Long) tableModel.getValueAt(row, 0);
            controller.deleteTeacher(id);
            JOptionPane.showMessageDialog(this, "Đã đặt trạng thái Inactive.");
            clearForm();
            loadTableData();
        }
    }

    private void clearForm() {
        txtFullName.setText(""); txtPhone.setText(""); txtEmail.setText("");
        txtSpecialty.setText(""); dpHireDate.clear();
        tblTeacher.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TeacherManagerFrame().setVisible(true));
    }
}
