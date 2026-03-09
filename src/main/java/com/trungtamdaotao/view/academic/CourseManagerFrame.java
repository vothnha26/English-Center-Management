package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.CourseController;
import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.model.entity.enums.CourseLevel;
import com.trungtamdaotao.model.entity.enums.DurationUnit;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CourseManagerFrame extends BaseManagerFrame {
    private final CourseController controller;
    private JTable tblCourse;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtCourseName, txtDuration, txtFee, txtSearch;
    private JTextArea txtDescription;
    private JComboBox<CourseLevel> cmbLevel;
    private JComboBox<DurationUnit> cmbDurationUnit;
    private JComboBox<Status> cmbStatus;
    
    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReload, btnFilterActive;
    
    // Selected course for update/delete
    private Course selectedCourse;

    public CourseManagerFrame() {
        super("Quản lý Khóa học");
        this.controller = new CourseController();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        // --- Toolbar (NORTH) ---
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlToolbar.setBackground(UIHelper.PRIMARY_COLOR);
        
        JLabel lblSearch = new JLabel("Tìm tên khóa học:");
        lblSearch.setForeground(Color.WHITE);
        lblSearch.setFont(UIHelper.BOLD_FONT);
        pnlToolbar.add(lblSearch);
        
        txtSearch = new JTextField(25);
        pnlToolbar.add(txtSearch);
        
        btnSearch = UIHelper.createStandardButton("Tìm", Color.WHITE, "🔍");
        btnSearch.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnSearch);
        
        btnFilterActive = UIHelper.createStandardButton("Đang mở", Color.WHITE, "✓");
        btnFilterActive.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnFilterActive);
        
        btnReload = UIHelper.createStandardButton("Tải lại", Color.WHITE, "⟳");
        btnReload.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnReload);
        
        add(pnlToolbar, BorderLayout.NORTH);

        // --- Form (WEST) ---
        JPanel pnlForm = UIHelper.createFormPanel("Thông tin khóa học");
        pnlForm.setPreferredSize(new Dimension(420, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFormField(pnlForm, "Tên khóa học: *", txtCourseName = new JTextField(), gbc, row++);
        
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Trình độ: *"), gbc);
        gbc.gridx = 1;
        cmbLevel = new JComboBox<>(CourseLevel.values());
        pnlForm.add(cmbLevel, gbc);
        row++;

        addFormField(pnlForm, "Thời lượng: *", txtDuration = new JTextField(), gbc, row++);
        
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Đơn vị:"), gbc);
        gbc.gridx = 1;
        cmbDurationUnit = new JComboBox<>(DurationUnit.values());
        pnlForm.add(cmbDurationUnit, gbc);
        row++;

        addFormField(pnlForm, "Học phí (VNĐ): *", txtFee = new JTextField(), gbc, row++);
        
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(Status.values());
        pnlForm.add(cmbStatus, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        pnlForm.add(createFieldLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        pnlForm.add(new JScrollPane(txtDescription), gbc);
        row++;

        // Buttons Panel
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlButtons.setOpaque(false);
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
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
        String[] columns = {"ID", "Tên khóa", "Trình độ", "Thời lượng", "Đơn vị", "Học phí", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCourse = new JTable(tableModel);
        setupTable(tblCourse);
        add(new JScrollPane(tblCourse), BorderLayout.CENTER);
    }

    private void addFormField(JPanel p, String label, JTextField tf, GridBagConstraints gbc, int r) {
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 1;
        p.add(createFieldLabel(label), gbc);
        gbc.gridx = 1;
        p.add(tf, gbc);
    }

    @Override
    protected void handleEvents() {
        btnSearch.addActionListener(e -> searchCourses());
        btnFilterActive.addActionListener(e -> filterActiveCourses());
        btnReload.addActionListener(e -> loadTableData());
        btnAdd.addActionListener(e -> addCourse());
        btnUpdate.addActionListener(e -> updateCourse());
        btnDelete.addActionListener(e -> deleteCourse());
        btnClear.addActionListener(e -> clearForm());
        
        tblCourse.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectCourseFromTable();
        });
    }

    private void addCourse() {
        try {
            Course c = new Course();
            c.setCourseName(txtCourseName.getText().trim());
            c.setLevel((CourseLevel) cmbLevel.getSelectedItem());
            if (!txtDuration.getText().isEmpty()) c.setDuration(Integer.parseInt(txtDuration.getText().trim()));
            c.setDurationUnit((DurationUnit) cmbDurationUnit.getSelectedItem());
            if (!txtFee.getText().isEmpty()) c.setFee(new BigDecimal(txtFee.getText().trim()));
            c.setStatus((Status) cmbStatus.getSelectedItem());
            c.setDescription(txtDescription.getText().trim());
            
            JOptionPane.showMessageDialog(this, controller.createCourse(c));
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void updateCourse() {
        if (selectedCourse == null) return;
        try {
            selectedCourse.setCourseName(txtCourseName.getText().trim());
            selectedCourse.setLevel((CourseLevel) cmbLevel.getSelectedItem());
            if (!txtDuration.getText().isEmpty()) selectedCourse.setDuration(Integer.parseInt(txtDuration.getText().trim()));
            selectedCourse.setDurationUnit((DurationUnit) cmbDurationUnit.getSelectedItem());
            if (!txtFee.getText().isEmpty()) selectedCourse.setFee(new BigDecimal(txtFee.getText().trim()));
            selectedCourse.setStatus((Status) cmbStatus.getSelectedItem());
            selectedCourse.setDescription(txtDescription.getText().trim());
            
            JOptionPane.showMessageDialog(this, controller.updateCourse(selectedCourse));
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void deleteCourse() {
        if (selectedCourse == null) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa khóa " + selectedCourse.getCourseName() + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, controller.deleteCourse(selectedCourse.getCourse_id().intValue()));
            loadTableData();
            clearForm();
        }
    }

    private void clearForm() {
        txtCourseName.setText(""); txtDuration.setText(""); txtFee.setText(""); txtDescription.setText("");
        cmbLevel.setSelectedIndex(0); cmbDurationUnit.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
        selectedCourse = null; tblCourse.clearSelection();
    }

    private void selectCourseFromTable() {
        int row = tblCourse.getSelectedRow();
        if (row >= 0) {
            Long id = (Long) tableModel.getValueAt(row, 0);
            selectedCourse = controller.getCourseById(id.intValue());
            if (selectedCourse != null) {
                txtCourseName.setText(selectedCourse.getCourseName());
                cmbLevel.setSelectedItem(selectedCourse.getLevel());
                txtDuration.setText(selectedCourse.getDuration() != null ? String.valueOf(selectedCourse.getDuration()) : "");
                cmbDurationUnit.setSelectedItem(selectedCourse.getDurationUnit());
                txtFee.setText(selectedCourse.getFee() != null ? selectedCourse.getFee().toString() : "");
                cmbStatus.setSelectedItem(selectedCourse.getStatus());
                txtDescription.setText(selectedCourse.getDescription() != null ? selectedCourse.getDescription() : "");
            }
        }
    }

    private void searchCourses() {
        renderTable(controller.searchCourses(txtSearch.getText().trim()));
    }

    private void filterActiveCourses() {
        renderTable(controller.getActiveCourses());
    }

    @Override
    protected void loadTableData() {
        if (controller != null) renderTable(controller.getAllCourses());
    }

    private void renderTable(List<Course> list) {
        tableModel.setRowCount(0);
        for (Course c : list) {
            tableModel.addRow(new Object[]{
                c.getCourse_id(), c.getCourseName(), c.getLevel(), c.getDuration(),
                c.getDurationUnit(), c.getFee() != null ? String.format("%,.0f", c.getFee()) : "0",
                c.getStatus()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CourseManagerFrame().setVisible(true));
    }
}
