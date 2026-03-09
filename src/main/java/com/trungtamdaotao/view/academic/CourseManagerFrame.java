package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.CourseController;
import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.model.entity.enums.CourseLevel;
import com.trungtamdaotao.model.entity.enums.DurationUnit;
import com.trungtamdaotao.model.entity.enums.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class CourseManagerFrame extends JFrame {
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
        this.controller = new CourseController();
        initComponents();
        loadTableData();
        
        setTitle("Quản lý Khóa học - MIS English Center");
        setSize(1400, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Toolbar phía trên
        JPanel pnlToolbar = createToolbarPanel();
        add(pnlToolbar, BorderLayout.NORTH);
        
        // Panel chính: Form bên trái + Table bên phải
        JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
        pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel pnlForm = createFormPanel();
        pnlMain.add(pnlForm, BorderLayout.WEST);
        
        JPanel pnlTable = createTablePanel();
        pnlMain.add(pnlTable, BorderLayout.CENTER);
        
        add(pnlMain, BorderLayout.CENTER);
    }

    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Công cụ tìm kiếm và lọc"));
        panel.setBackground(new Color(240, 248, 255));
        
        panel.add(new JLabel("Tìm tên khóa học:"));
        txtSearch = new JTextField(25);
        panel.add(txtSearch);
        
        btnSearch = new JButton("🔍 Tìm kiếm");
        btnFilterActive = new JButton("✓ Khóa học đang mở");
        btnReload = new JButton("⟳ Tải lại tất cả");
        
        panel.add(btnSearch);
        panel.add(btnFilterActive);
        panel.add(btnReload);
        
        // Event handlers
        btnSearch.addActionListener(e -> searchCourses());
        btnFilterActive.addActionListener(e -> filterActiveCourses());
        btnReload.addActionListener(e -> loadTableData());
        
        // Enter key for search
        txtSearch.addActionListener(e -> searchCourses());
        
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Thông tin khóa học"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(420, 650));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Course Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tên khóa học: *"), gbc);
        gbc.gridx = 1;
        txtCourseName = new JTextField(20);
        panel.add(txtCourseName, gbc);
        row++;
        
        // Level
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Trình độ: *"), gbc);
        gbc.gridx = 1;
        cmbLevel = new JComboBox<>(CourseLevel.values());
        panel.add(cmbLevel, gbc);
        row++;
        
        // Duration
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Thời lượng: *"), gbc);
        gbc.gridx = 1;
        txtDuration = new JTextField(20);
        panel.add(txtDuration, gbc);
        row++;
        
        // Duration Unit
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Đơn vị:"), gbc);
        gbc.gridx = 1;
        cmbDurationUnit = new JComboBox<>(DurationUnit.values());
        panel.add(cmbDurationUnit, gbc);
        row++;
        
        // Fee
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Học phí (VNĐ): *"), gbc);
        gbc.gridx = 1;
        txtFee = new JTextField(20);
        panel.add(txtFee, gbc);
        row++;
        
        // Status
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(Status.values());
        panel.add(cmbStatus, gbc);
        row++;
        
        // Description
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtDescription = new JTextArea(5, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        panel.add(scrollDesc, gbc);
        row++;
        
        // Buttons panel
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        
        btnAdd = new JButton("➕ Thêm mới");
        btnUpdate = new JButton("✏️ Cập nhật");
        btnDelete = new JButton("🗑️ Xóa");
        btnClear = new JButton("🔄 Làm mới form");
        
        // Set colors
        btnAdd.setBackground(new Color(76, 175, 80));
        btnAdd.setForeground(Color.WHITE);
        btnUpdate.setBackground(new Color(33, 150, 243));
        btnUpdate.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        
        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnClear);
        
        panel.add(pnlButtons, gbc);
        
        // Event handlers
        btnAdd.addActionListener(e -> addCourse());
        btnUpdate.addActionListener(e -> updateCourse());
        btnDelete.addActionListener(e -> deleteCourse());
        btnClear.addActionListener(e -> clearForm());
        
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách khóa học"));
        
        // Table
        String[] columns = {"ID", "Tên khóa học", "Trình độ", "Thời lượng", "Đơn vị", "Học phí (VNĐ)", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCourse = new JTable(tableModel);
        tblCourse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCourse.setRowHeight(25);
        tblCourse.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tblCourse);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Table selection listener
        tblCourse.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectCourseFromTable();
            }
        });
        
        // Status bar
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlStatus.add(new JLabel("💡 Nhấp đúp vào dòng để chọn và chỉnh sửa"));
        panel.add(pnlStatus, BorderLayout.SOUTH);
        
        return panel;
    }

    private void addCourse() {
        try {
            Course course = new Course();
            course.setCourseName(txtCourseName.getText().trim());
            course.setLevel((CourseLevel) cmbLevel.getSelectedItem());
            
            String durationStr = txtDuration.getText().trim();
            if (!durationStr.isEmpty()) {
                course.setDuration(Integer.parseInt(durationStr));
            }
            
            course.setDurationUnit((DurationUnit) cmbDurationUnit.getSelectedItem());
            
            String feeStr = txtFee.getText().trim();
            if (!feeStr.isEmpty()) {
                course.setFee(new BigDecimal(feeStr));
            }
            
            course.setStatus((Status) cmbStatus.getSelectedItem());
            course.setDescription(txtDescription.getText().trim());
            
            String message = controller.createCourse(course);
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Thời lượng và học phí phải là số hợp lệ!", 
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCourse() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học từ bảng để cập nhật!", 
                    "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            selectedCourse.setCourseName(txtCourseName.getText().trim());
            selectedCourse.setLevel((CourseLevel) cmbLevel.getSelectedItem());
            
            String durationStr = txtDuration.getText().trim();
            if (!durationStr.isEmpty()) {
                selectedCourse.setDuration(Integer.parseInt(durationStr));
            }
            
            selectedCourse.setDurationUnit((DurationUnit) cmbDurationUnit.getSelectedItem());
            
            String feeStr = txtFee.getText().trim();
            if (!feeStr.isEmpty()) {
                selectedCourse.setFee(new BigDecimal(feeStr));
            }
            
            selectedCourse.setStatus((Status) cmbStatus.getSelectedItem());
            selectedCourse.setDescription(txtDescription.getText().trim());
            
            String message = controller.updateCourse(selectedCourse);
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Thời lượng và học phí phải là số hợp lệ!", 
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCourse() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khóa học từ bảng để xóa!", 
                    "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa khóa học:\n'" + selectedCourse.getCourseName() + "'?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String message = controller.deleteCourse(selectedCourse.getCourse_id().intValue());
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtCourseName.setText("");
        txtDuration.setText("");
        txtFee.setText("");
        txtDescription.setText("");
        cmbLevel.setSelectedIndex(0);
        cmbDurationUnit.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        selectedCourse = null;
        tblCourse.clearSelection();
    }

    private void selectCourseFromTable() {
        int selectedRow = tblCourse.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
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
        String keyword = txtSearch.getText().trim();
        List<Course> courses = controller.searchCourses(keyword);
        renderTable(courses);
    }

    private void filterActiveCourses() {
        List<Course> courses = controller.getActiveCourses();
        renderTable(courses);
    }

    private void loadTableData() {
        List<Course> courses = controller.getAllCourses();
        renderTable(courses);
    }

    private void renderTable(List<Course> courses) {
        tableModel.setRowCount(0);
        if (courses != null && !courses.isEmpty()) {
            for (Course c : courses) {
                tableModel.addRow(new Object[]{
                        c.getCourse_id(),
                        c.getCourseName(),
                        c.getLevel() != null ? c.getLevel().name() : "N/A",
                        c.getDuration() != null ? c.getDuration() : "",
                        c.getDurationUnit() != null ? c.getDurationUnit().name() : "N/A",
                        c.getFee() != null ? String.format("%,.0f", c.getFee()) : "0",
                        c.getStatus() != null ? c.getStatus().name() : "N/A"
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CourseManagerFrame().setVisible(true));
    }
}
