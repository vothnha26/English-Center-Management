package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.AttendanceController;
import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.model.dao.impl.StudentDAOImpl;
import com.trungtamdaotao.model.dao.system.IStudentDAO;
import com.trungtamdaotao.model.entity.academic.Attendance;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.AttendanceStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AttendanceManagerFrame extends JFrame {
    private final AttendanceController attendanceController;
    private final ClassController classController;
    private final IStudentDAO studentDAO;
    
    private JTable tblAttendance;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtAttendDate, txtNote;
    private JTextField txtSearchDate, txtSearchStudent;
    private JComboBox<Student> cmbStudent;
    private JComboBox<ClassEntity> cmbClass;
    private JComboBox<AttendanceStatus> cmbStatus;
    
    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JButton btnSearchByDate, btnSearchByClass, btnSearchByStudent;
    private JButton btnSearchByStatus, btnReload, btnRefreshCombo;
    private JButton btnStatistics;
    
    // Selected attendance for update/delete
    private Attendance selectedAttendance;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AttendanceManagerFrame() {
        this.attendanceController = new AttendanceController();
        this.classController = new ClassController();
        this.studentDAO = new StudentDAOImpl();
        
        initComponents();
        loadComboBoxData();
        loadTableData();
        
        setTitle("Quản lý Điểm danh - MIS English Center");
        setSize(1600, 800);
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Công cụ tìm kiếm và lọc"));
        panel.setBackground(new Color(240, 248, 255));
        
        // Search by date
        panel.add(new JLabel("Tìm theo ngày:"));
        txtSearchDate = new JTextField(12);
        txtSearchDate.setToolTipText("yyyy-MM-dd");
        panel.add(txtSearchDate);
        
        btnSearchByDate = new JButton("🔍 Tìm theo ngày");
        panel.add(btnSearchByDate);
        
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Search by student
        panel.add(new JLabel("Tìm học viên:"));
        txtSearchStudent = new JTextField(15);
        panel.add(txtSearchStudent);
        
        btnSearchByStudent = new JButton("👤 Tìm học viên");
        panel.add(btnSearchByStudent);
        
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Other filters
        btnSearchByClass = new JButton("📚 Lọc theo lớp");
        btnSearchByStatus = new JButton("📊 Lọc theo trạng thái");
        btnStatistics = new JButton("📈 Thống kê");
        btnReload = new JButton("⟳ Tải lại tất cả");
        
        panel.add(btnSearchByClass);
        panel.add(btnSearchByStatus);
        panel.add(btnStatistics);
        panel.add(btnReload);
        
        // Event handlers
        btnSearchByDate.addActionListener(e -> searchByDate());
        btnSearchByStudent.addActionListener(e -> searchByStudent());
        btnSearchByClass.addActionListener(e -> filterByClass());
        btnSearchByStatus.addActionListener(e -> filterByStatus());
        btnStatistics.addActionListener(e -> showStatistics());
        btnReload.addActionListener(e -> loadTableData());
        
        // Enter key for search
        txtSearchDate.addActionListener(e -> searchByDate());
        txtSearchStudent.addActionListener(e -> searchByStudent());
        
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Thông tin điểm danh"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(450, 700));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Student
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Học viên: *"), gbc);
        gbc.gridx = 1;
        JPanel pnlStudent = new JPanel(new BorderLayout(5, 0));
        cmbStudent = new JComboBox<>();
        pnlStudent.add(cmbStudent, BorderLayout.CENTER);
        btnRefreshCombo = new JButton("⟳");
        btnRefreshCombo.setPreferredSize(new Dimension(40, 25));
        btnRefreshCombo.setToolTipText("Làm mới danh sách");
        btnRefreshCombo.addActionListener(e -> loadComboBoxData());
        pnlStudent.add(btnRefreshCombo, BorderLayout.EAST);
        panel.add(pnlStudent, gbc);
        row++;
        
        // Class
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Lớp học: *"), gbc);
        gbc.gridx = 1;
        cmbClass = new JComboBox<>();
        panel.add(cmbClass, gbc);
        row++;
        
        // Attend Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Ngày điểm danh: *"), gbc);
        gbc.gridx = 1;
        txtAttendDate = new JTextField(20);
        txtAttendDate.setToolTipText("Định dạng: yyyy-MM-dd (VD: 2026-03-10)");
        panel.add(txtAttendDate, gbc);
        row++;
        
        // Status
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Trạng thái: *"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(AttendanceStatus.values());
        panel.add(cmbStatus, gbc);
        row++;
        
        // Note
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        txtNote = new JTextField(20);
        panel.add(txtNote, gbc);
        row++;
        
        // Status descriptions
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel pnlStatusDesc = new JPanel();
        pnlStatusDesc.setLayout(new BoxLayout(pnlStatusDesc, BoxLayout.Y_AXIS));
        pnlStatusDesc.setBorder(BorderFactory.createTitledBorder("Trạng thái"));
        pnlStatusDesc.add(new JLabel("• Present: Có mặt"));
        pnlStatusDesc.add(new JLabel("• Absent: Vắng mặt"));
        pnlStatusDesc.add(new JLabel("• Late: Đi trễ"));
        panel.add(pnlStatusDesc, gbc);
        row++;
        
        // Note panel
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel pnlNote = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNote.add(new JLabel("<html><i>* Trường bắt buộc<br>" +
                "Định dạng ngày: yyyy-MM-dd</i></html>"));
        panel.add(pnlNote, gbc);
        row++;
        
        // Spacer
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weighty = 0.5;
        panel.add(Box.createVerticalStrut(10), gbc);
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
        btnAdd.addActionListener(e -> addAttendance());
        btnUpdate.addActionListener(e -> updateAttendance());
        btnDelete.addActionListener(e -> deleteAttendance());
        btnClear.addActionListener(e -> clearForm());
        
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách điểm danh"));
        
        // Table
        String[] columns = {"ID", "Học viên", "Lớp học", "Khóa học", 
                           "Ngày điểm danh", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblAttendance = new JTable(tableModel);
        tblAttendance.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAttendance.setRowHeight(25);
        tblAttendance.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Set column widths
        tblAttendance.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblAttendance.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblAttendance.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblAttendance.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblAttendance.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblAttendance.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblAttendance.getColumnModel().getColumn(6).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(tblAttendance);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Table selection listener
        tblAttendance.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectAttendanceFromTable();
            }
        });
        
        // Status bar
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlStatus.add(new JLabel("💡 Nhấp đúp vào dòng để chọn và chỉnh sửa"));
        panel.add(pnlStatus, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadComboBoxData() {
        // Load students
        List<Student> students = studentDAO.findAll();
        cmbStudent.removeAllItems();
        for (Student s : students) {
            cmbStudent.addItem(s);
        }
        
        // Custom renderer for students
        cmbStudent.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    Student s = (Student) value;
                    setText(s.getFullName() + " (" + 
                            (s.getPhone() != null ? s.getPhone() : "N/A") + ")");
                }
                return this;
            }
        });
        
        // Load classes
        List<ClassEntity> classes = classController.getAllClasses();
        cmbClass.removeAllItems();
        for (ClassEntity c : classes) {
            cmbClass.addItem(c);
        }
        
        // Custom renderer for classes
        cmbClass.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ClassEntity) {
                    ClassEntity c = (ClassEntity) value;
                    setText(c.getClassName() + " (" + 
                            (c.getCourse() != null ? c.getCourse().getCourseName() : "N/A") + ")");
                }
                return this;
            }
        });
    }

    private void addAttendance() {
        try {
            Attendance attendance = new Attendance();
            attendance.setStudent((Student) cmbStudent.getSelectedItem());
            attendance.setClazz((ClassEntity) cmbClass.getSelectedItem());
            
            // Parse attend date
            String attendDateStr = txtAttendDate.getText().trim();
            if (!attendDateStr.isEmpty()) {
                try {
                    attendance.setAttend_date(LocalDate.parse(attendDateStr, dateFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Lỗi: Ngày điểm danh không đúng định dạng (yyyy-MM-dd)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            attendance.setStatus((AttendanceStatus) cmbStatus.getSelectedItem());
            attendance.setNote(txtNote.getText().trim());
            
            String message = attendanceController.createAttendance(attendance);
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAttendance() {
        if (selectedAttendance == null) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn điểm danh từ bảng để cập nhật!", 
                    "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            selectedAttendance.setStudent((Student) cmbStudent.getSelectedItem());
            selectedAttendance.setClazz((ClassEntity) cmbClass.getSelectedItem());
            
            // Parse attend date
            String attendDateStr = txtAttendDate.getText().trim();
            if (!attendDateStr.isEmpty()) {
                try {
                    selectedAttendance.setAttend_date(LocalDate.parse(attendDateStr, dateFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Lỗi: Ngày điểm danh không đúng định dạng (yyyy-MM-dd)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            selectedAttendance.setStatus((AttendanceStatus) cmbStatus.getSelectedItem());
            selectedAttendance.setNote(txtNote.getText().trim());
            
            String message = attendanceController.updateAttendance(selectedAttendance);
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAttendance() {
        if (selectedAttendance == null) {
            JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn điểm danh từ bảng để xóa!", 
                    "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa điểm danh này?\n" +
                "Học viên: " + (selectedAttendance.getStudent() != null ? 
                        selectedAttendance.getStudent().getFullName() : "N/A") + "\n" +
                "Lớp: " + (selectedAttendance.getClazz() != null ? 
                        selectedAttendance.getClazz().getClassName() : "N/A") + "\n" +
                "Ngày: " + selectedAttendance.getAttend_date(),
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String message = attendanceController.deleteAttendance(
                    selectedAttendance.getAttendance_id().intValue());
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtAttendDate.setText("");
        txtNote.setText("");
        if (cmbStudent.getItemCount() > 0) cmbStudent.setSelectedIndex(0);
        if (cmbClass.getItemCount() > 0) cmbClass.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        selectedAttendance = null;
        tblAttendance.clearSelection();
    }

    private void selectAttendanceFromTable() {
        int selectedRow = tblAttendance.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            selectedAttendance = attendanceController.getAttendanceById(id.intValue());
            
            if (selectedAttendance != null) {
                // Tìm và chọn Student theo ID
                if (selectedAttendance.getStudent() != null) {
                    Long studentId = selectedAttendance.getStudent().getStudent_id();
                    for (int i = 0; i < cmbStudent.getItemCount(); i++) {
                        Student s = cmbStudent.getItemAt(i);
                        if (s != null && s.getStudent_id().equals(studentId)) {
                            cmbStudent.setSelectedIndex(i);
                            break;
                        }
                    }
                } else if (cmbStudent.getItemCount() > 0) {
                    cmbStudent.setSelectedIndex(0);
                }
                
                // Tìm và chọn Class theo ID
                if (selectedAttendance.getClazz() != null) {
                    Long classId = selectedAttendance.getClazz().getClass_id();
                    for (int i = 0; i < cmbClass.getItemCount(); i++) {
                        ClassEntity c = cmbClass.getItemAt(i);
                        if (c != null && c.getClass_id().equals(classId)) {
                            cmbClass.setSelectedIndex(i);
                            break;
                        }
                    }
                } else if (cmbClass.getItemCount() > 0) {
                    cmbClass.setSelectedIndex(0);
                }
                
                txtAttendDate.setText(selectedAttendance.getAttend_date() != null ? 
                        selectedAttendance.getAttend_date().format(dateFormatter) : "");
                cmbStatus.setSelectedItem(selectedAttendance.getStatus());
                txtNote.setText(selectedAttendance.getNote() != null ? 
                        selectedAttendance.getNote() : "");
            }
        }
    }

    private void searchByDate() {
        String dateStr = txtSearchDate.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày cần tìm!", 
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            List<Attendance> attendances = attendanceController.getAttendancesByDate(date);
            renderTable(attendances);
            JOptionPane.showMessageDialog(this, 
                    "Tìm thấy " + attendances.size() + " điểm danh vào ngày " + date,
                    "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Lỗi: Ngày không đúng định dạng (yyyy-MM-dd)!", 
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchByStudent() {
        String keyword = txtSearchStudent.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên học viên!", 
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Student> students = studentDAO.searchByName(keyword);
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy học viên nào!", 
                    "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Nếu tìm thấy nhiều, hiển thị dialog chọn
        Student selectedStudent;
        if (students.size() == 1) {
            selectedStudent = students.get(0);
        } else {
            selectedStudent = (Student) JOptionPane.showInputDialog(this,
                    "Chọn học viên:", "Nhiều kết quả",
                    JOptionPane.QUESTION_MESSAGE, null,
                    students.toArray(), students.get(0));
        }
        
        if (selectedStudent != null) {
            List<Attendance> attendances = attendanceController.getAttendancesByStudent(selectedStudent);
            renderTable(attendances);
            JOptionPane.showMessageDialog(this, 
                    "Tìm thấy " + attendances.size() + " điểm danh của " + 
                    selectedStudent.getFullName(),
                    "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterByClass() {
        ClassEntity selectedClass = (ClassEntity) cmbClass.getSelectedItem();
        if (selectedClass != null) {
            List<Attendance> attendances = attendanceController.getAttendancesByClass(selectedClass);
            renderTable(attendances);
            JOptionPane.showMessageDialog(this, 
                    "Đã lọc " + attendances.size() + " điểm danh của lớp: " + 
                    selectedClass.getClassName(),
                    "Kết quả lọc", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterByStatus() {
        AttendanceStatus selectedStatus = (AttendanceStatus) cmbStatus.getSelectedItem();
        if (selectedStatus != null) {
            List<Attendance> attendances = attendanceController.getAttendancesByStatus(selectedStatus);
            renderTable(attendances);
            JOptionPane.showMessageDialog(this, 
                    "Đã lọc " + attendances.size() + " điểm danh có trạng thái: " + 
                    selectedStatus.name(),
                    "Kết quả lọc", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showStatistics() {
        ClassEntity selectedClass = (ClassEntity) cmbClass.getSelectedItem();
        if (selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học!", 
                    "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Hiển thị dialog nhập khoảng thời gian
        JTextField txtStart = new JTextField(10);
        JTextField txtEnd = new JTextField(10);
        
        JPanel pnlInput = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlInput.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        pnlInput.add(txtStart);
        pnlInput.add(new JLabel("Đến ngày (yyyy-MM-dd):"));
        pnlInput.add(txtEnd);
        
        int result = JOptionPane.showConfirmDialog(this, pnlInput, 
                "Nhập khoảng thời gian thống kê", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalDate startDate = LocalDate.parse(txtStart.getText().trim(), dateFormatter);
                LocalDate endDate = LocalDate.parse(txtEnd.getText().trim(), dateFormatter);
                
                double rate = attendanceController.getAttendanceRate(selectedClass, startDate, endDate);
                
                JOptionPane.showMessageDialog(this, 
                        String.format("Tỷ lệ có mặt của lớp %s\nTừ %s đến %s:\n%.2f%%",
                                selectedClass.getClassName(), startDate, endDate, rate),
                        "Thống kê", JOptionPane.INFORMATION_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, 
                        "Lỗi: Ngày không đúng định dạng (yyyy-MM-dd)!", 
                        "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTableData() {
        List<Attendance> attendances = attendanceController.getAllAttendances();
        renderTable(attendances);
    }

    private void renderTable(List<Attendance> attendances) {
        tableModel.setRowCount(0);
        if (attendances != null && !attendances.isEmpty()) {
            for (Attendance a : attendances) {
                tableModel.addRow(new Object[]{
                        a.getAttendance_id(),
                        a.getStudent() != null ? a.getStudent().getFullName() : "N/A",
                        a.getClazz() != null ? a.getClazz().getClassName() : "N/A",
                        a.getClazz() != null && a.getClazz().getCourse() != null ? 
                                a.getClazz().getCourse().getCourseName() : "N/A",
                        a.getAttend_date() != null ? a.getAttend_date().format(dateFormatter) : "",
                        a.getStatus() != null ? a.getStatus().name() : "N/A",
                        a.getNote() != null ? a.getNote() : ""
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AttendanceManagerFrame().setVisible(true));
    }
}
