package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.academic.CourseController;
import com.trungtamdaotao.model.dao.impl.RoomDAOImpl;
import com.trungtamdaotao.model.dao.impl.TeacherDAOImpl;
import com.trungtamdaotao.model.dao.operations.IRoomDAO;
import com.trungtamdaotao.model.dao.system.ITeacherDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.ClassStatus;
import com.trungtamdaotao.model.entity.operations.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ClassManagerFrame extends JFrame {
    private final ClassController classController;
    private final CourseController courseController;
    private final ITeacherDAO teacherDAO;
    private final IRoomDAO roomDAO;
    
    private JTable tblClass;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtClassName, txtMaxStudent, txtStartDate, txtEndDate, txtSearch;
    private JComboBox<Course> cmbCourse;
    private JComboBox<Teacher> cmbTeacher;
    private JComboBox<Room> cmbRoom;
    private JComboBox<ClassStatus> cmbStatus;
    
    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JButton btnSearch, btnReload, btnFilterByCourse, btnRefreshCombo;
    
    // Selected class for update/delete
    private ClassEntity selectedClass;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ClassManagerFrame() {
        this.classController = new ClassController();
        this.courseController = new CourseController();
        this.teacherDAO = new TeacherDAOImpl();
        this.roomDAO = new RoomDAOImpl();
        
        initComponents();
        loadComboBoxData();
        loadTableData();
        
        setTitle("Quản lý Lớp học - MIS English Center");
        setSize(1500, 800);
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
        
        panel.add(new JLabel("Tìm tên lớp:"));
        txtSearch = new JTextField(25);
        panel.add(txtSearch);
        
        btnSearch = new JButton("🔍 Tìm kiếm");
        btnFilterByCourse = new JButton("📚 Lọc theo khóa học");
        btnReload = new JButton("⟳ Tải lại tất cả");
        
        panel.add(btnSearch);
        panel.add(btnFilterByCourse);
        panel.add(btnReload);
        
        // Event handlers
        btnSearch.addActionListener(e -> searchClasses());
        btnFilterByCourse.addActionListener(e -> filterByCourse());
        btnReload.addActionListener(e -> loadTableData());
        
        // Enter key for search
        txtSearch.addActionListener(e -> searchClasses());
        
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Thông tin lớp học"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(450, 700));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Class Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tên lớp học: *"), gbc);
        gbc.gridx = 1;
        txtClassName = new JTextField(20);
        panel.add(txtClassName, gbc);
        row++;
        
        // Course
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Khóa học: *"), gbc);
        gbc.gridx = 1;
        JPanel pnlCourse = new JPanel(new BorderLayout(5, 0));
        cmbCourse = new JComboBox<>();
        pnlCourse.add(cmbCourse, BorderLayout.CENTER);
        btnRefreshCombo = new JButton("⟳");
        btnRefreshCombo.setPreferredSize(new Dimension(40, 25));
        btnRefreshCombo.setToolTipText("Làm mới danh sách");
        btnRefreshCombo.addActionListener(e -> loadComboBoxData());
        pnlCourse.add(btnRefreshCombo, BorderLayout.EAST);
        panel.add(pnlCourse, gbc);
        row++;
        
        // Teacher
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Giáo viên:"), gbc);
        gbc.gridx = 1;
        cmbTeacher = new JComboBox<>();
        panel.add(cmbTeacher, gbc);
        row++;
        
        // Room
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Phòng học:"), gbc);
        gbc.gridx = 1;
        cmbRoom = new JComboBox<>();
        panel.add(cmbRoom, gbc);
        row++;
        
        // Start Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1;
        txtStartDate = new JTextField(20);
        txtStartDate.setToolTipText("Định dạng: yyyy-MM-dd (VD: 2026-03-10)");
        panel.add(txtStartDate, gbc);
        row++;
        
        // End Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 1;
        txtEndDate = new JTextField(20);
        txtEndDate.setToolTipText("Định dạng: yyyy-MM-dd (VD: 2026-06-10)");
        panel.add(txtEndDate, gbc);
        row++;
        
        // Max Student
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Sĩ số tối đa: *"), gbc);
        gbc.gridx = 1;
        txtMaxStudent = new JTextField(20);
        panel.add(txtMaxStudent, gbc);
        row++;
        
        // Status
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(ClassStatus.values());
        panel.add(cmbStatus, gbc);
        row++;
        
        // Note panel
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel pnlNote = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNote.add(new JLabel("<html><i>* Trường bắt buộc | Ngày: yyyy-MM-dd</i></html>"));
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
        btnAdd.addActionListener(e -> addClass());
        btnUpdate.addActionListener(e -> updateClass());
        btnDelete.addActionListener(e -> deleteClass());
        btnClear.addActionListener(e -> clearForm());
        
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách lớp học"));
        
        // Table
        String[] columns = {"ID", "Tên lớp", "Khóa học", "Giáo viên", "Phòng", 
                           "Ngày bắt đầu", "Ngày kết thúc", "Sĩ số", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblClass = new JTable(tableModel);
        tblClass.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblClass.setRowHeight(25);
        tblClass.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tblClass);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Table selection listener
        tblClass.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectClassFromTable();
            }
        });
        
        // Status bar
        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlStatus.add(new JLabel("💡 Nhấp đúp vào dòng để chọn và chỉnh sửa"));
        panel.add(pnlStatus, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadComboBoxData() {
        // Load courses
        List<Course> courses = courseController.getActiveCourses();
        cmbCourse.removeAllItems();
        for (Course c : courses) {
            cmbCourse.addItem(c);
        }
        
        // Load teachers (thêm null option)
        List<Teacher> teachers = teacherDAO.findAll();
        cmbTeacher.removeAllItems();
        cmbTeacher.addItem(null); // Option để không chọn teacher
        for (Teacher t : teachers) {
            cmbTeacher.addItem(t);
        }
        
        // Load rooms (thêm null option)
        List<Room> rooms = roomDAO.findAll();
        cmbRoom.removeAllItems();
        cmbRoom.addItem(null); // Option để không chọn room
        for (Room r : rooms) {
            cmbRoom.addItem(r);
        }
        
        // Custom renderer for null values
        cmbTeacher.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Chưa phân công --");
                } else if (value instanceof Teacher) {
                    setText(((Teacher) value).getFullName());
                }
                return this;
            }
        });
        
        cmbRoom.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Chưa phân phòng --");
                } else if (value instanceof Room) {
                    setText(((Room) value).getRoomName());
                }
                return this;
            }
        });
    }

    private void addClass() {
        try {
            ClassEntity classEntity = new ClassEntity();
            classEntity.setClassName(txtClassName.getText().trim());
            classEntity.setCourse((Course) cmbCourse.getSelectedItem());
            classEntity.setTeacher((Teacher) cmbTeacher.getSelectedItem());
            classEntity.setRoom((Room) cmbRoom.getSelectedItem());
            
            // Parse dates
            String startDateStr = txtStartDate.getText().trim();
            if (!startDateStr.isEmpty()) {
                try {
                    classEntity.setStartDate(LocalDate.parse(startDateStr, dateFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Ngày bắt đầu không đúng định dạng (yyyy-MM-dd)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            String endDateStr = txtEndDate.getText().trim();
            if (!endDateStr.isEmpty()) {
                try {
                    classEntity.setEndDate(LocalDate.parse(endDateStr, dateFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Ngày kết thúc không đúng định dạng (yyyy-MM-dd)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            String maxStudentStr = txtMaxStudent.getText().trim();
            if (!maxStudentStr.isEmpty()) {
                classEntity.setMaxStudent(Integer.parseInt(maxStudentStr));
            }
            
            classEntity.setStatus((ClassStatus) cmbStatus.getSelectedItem());
            
            String message = classController.createClass(classEntity);
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Số lượng học viên phải là số hợp lệ!", 
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateClass() {
        if (selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học từ bảng để cập nhật!", 
                    "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            selectedClass.setClassName(txtClassName.getText().trim());
            selectedClass.setCourse((Course) cmbCourse.getSelectedItem());
            selectedClass.setTeacher((Teacher) cmbTeacher.getSelectedItem());
            selectedClass.setRoom((Room) cmbRoom.getSelectedItem());
            
            // Parse dates
            String startDateStr = txtStartDate.getText().trim();
            if (!startDateStr.isEmpty()) {
                try {
                    selectedClass.setStartDate(LocalDate.parse(startDateStr, dateFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Ngày bắt đầu không đúng định dạng (yyyy-MM-dd)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            String endDateStr = txtEndDate.getText().trim();
            if (!endDateStr.isEmpty()) {
                try {
                    selectedClass.setEndDate(LocalDate.parse(endDateStr, dateFormatter));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Ngày kết thúc không đúng định dạng (yyyy-MM-dd)!", 
                            "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            String maxStudentStr = txtMaxStudent.getText().trim();
            if (!maxStudentStr.isEmpty()) {
                selectedClass.setMaxStudent(Integer.parseInt(maxStudentStr));
            }
            
            selectedClass.setStatus((ClassStatus) cmbStatus.getSelectedItem());
            
            String message = classController.updateClass(selectedClass);
            
            if (message.contains("thành công")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: Số lượng học viên phải là số hợp lệ!", 
                    "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteClass() {
        if (selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học từ bảng để xóa!", 
                    "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa lớp học:\n'" + selectedClass.getClassName() + "'?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String message = classController.deleteClass(selectedClass.getClass_id().intValue());
            
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
        txtClassName.setText("");
        txtStartDate.setText("");
        txtEndDate.setText("");
        txtMaxStudent.setText("");
        if (cmbCourse.getItemCount() > 0) cmbCourse.setSelectedIndex(0);
        if (cmbTeacher.getItemCount() > 0) cmbTeacher.setSelectedIndex(0);
        if (cmbRoom.getItemCount() > 0) cmbRoom.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        selectedClass = null;
        tblClass.clearSelection();
    }

    private void selectClassFromTable() {
        int selectedRow = tblClass.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            selectedClass = classController.getClassById(id.intValue());
            
            if (selectedClass != null) {
                txtClassName.setText(selectedClass.getClassName());
                
                // Tìm và chọn Course theo ID
                if (selectedClass.getCourse() != null) {
                    Long courseId = selectedClass.getCourse().getCourse_id();
                    for (int i = 0; i < cmbCourse.getItemCount(); i++) {
                        Course c = cmbCourse.getItemAt(i);
                        if (c != null && c.getCourse_id().equals(courseId)) {
                            cmbCourse.setSelectedIndex(i);
                            break;
                        }
                    }
                } else if (cmbCourse.getItemCount() > 0) {
                    cmbCourse.setSelectedIndex(0);
                }
                
                // Tìm và chọn Teacher theo ID
                if (selectedClass.getTeacher() != null) {
                    Long teacherId = selectedClass.getTeacher().getTeacher_id();
                    for (int i = 0; i < cmbTeacher.getItemCount(); i++) {
                        Teacher t = cmbTeacher.getItemAt(i);
                        if (t != null && t.getTeacher_id().equals(teacherId)) {
                            cmbTeacher.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    cmbTeacher.setSelectedIndex(0); // Chọn "-- Chưa phân công --"
                }
                
                // Tìm và chọn Room theo ID
                if (selectedClass.getRoom() != null) {
                    Long roomId = selectedClass.getRoom().getRoom_id();
                    for (int i = 0; i < cmbRoom.getItemCount(); i++) {
                        Room r = cmbRoom.getItemAt(i);
                        if (r != null && r.getRoom_id().equals(roomId)) {
                            cmbRoom.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    cmbRoom.setSelectedIndex(0); // Chọn "-- Chưa phân phòng --"
                }
                
                txtStartDate.setText(selectedClass.getStartDate() != null ? 
                        selectedClass.getStartDate().format(dateFormatter) : "");
                txtEndDate.setText(selectedClass.getEndDate() != null ? 
                        selectedClass.getEndDate().format(dateFormatter) : "");
                txtMaxStudent.setText(String.valueOf(selectedClass.getMaxStudent()));
                cmbStatus.setSelectedItem(selectedClass.getStatus());
            }
        }
    }

    private void searchClasses() {
        String keyword = txtSearch.getText().trim();
        List<ClassEntity> classes = classController.searchClasses(keyword);
        renderTable(classes);
    }

    private void filterByCourse() {
        Course selectedCourse = (Course) cmbCourse.getSelectedItem();
        if (selectedCourse != null) {
            List<ClassEntity> classes = classController.getClassesByCourse(selectedCourse);
            renderTable(classes);
            JOptionPane.showMessageDialog(this, 
                    "Đã lọc " + classes.size() + " lớp học thuộc khóa: " + selectedCourse.getCourseName(),
                    "Kết quả lọc", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadTableData() {
        List<ClassEntity> classes = classController.getAllClasses();
        renderTable(classes);
    }

    private void renderTable(List<ClassEntity> classes) {
        tableModel.setRowCount(0);
        if (classes != null && !classes.isEmpty()) {
            for (ClassEntity c : classes) {
                tableModel.addRow(new Object[]{
                        c.getClass_id(),
                        c.getClassName(),
                        c.getCourse() != null ? c.getCourse().getCourseName() : "N/A",
                        c.getTeacher() != null ? c.getTeacher().getFullName() : "Chưa có",
                        c.getRoom() != null ? c.getRoom().getRoomName() : "Chưa có",
                        c.getStartDate() != null ? c.getStartDate().format(dateFormatter) : "",
                        c.getEndDate() != null ? c.getEndDate().format(dateFormatter) : "",
                        c.getMaxStudent(),
                        c.getStatus() != null ? c.getStatus().name() : "N/A"
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClassManagerFrame().setVisible(true));
    }
}
