package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.academic.CourseController;
import com.trungtamdaotao.model.dao.operations.impl.RoomDAOImpl;
import com.trungtamdaotao.model.dao.teacher.impl.TeacherDAOImpl;
import com.trungtamdaotao.model.dao.operations.IRoomDAO;
import com.trungtamdaotao.model.dao.teacher.ITeacherDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Course;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.ClassStatus;
import com.trungtamdaotao.model.entity.operations.Room;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ClassManagerFrame extends BaseManagerFrame {
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
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReload, btnFilterByCourse;
    
    // Selected class for update/delete
    private ClassEntity selectedClass;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ClassManagerFrame() {
        super("Quản lý Lớp học");
        this.classController = new ClassController();
        this.courseController = new CourseController();
        this.teacherDAO = new TeacherDAOImpl();
        this.roomDAO = new RoomDAOImpl();
        
        loadComboBoxData();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        // --- Toolbar (NORTH) ---
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlToolbar.setBackground(UIHelper.PRIMARY_COLOR);
        
        JLabel lblSearch = new JLabel("Tìm tên lớp:");
        lblSearch.setForeground(Color.WHITE);
        lblSearch.setFont(UIHelper.BOLD_FONT);
        pnlToolbar.add(lblSearch);
        
        txtSearch = new JTextField(20);
        pnlToolbar.add(txtSearch);
        
        btnSearch = UIHelper.createStandardButton("Tìm", Color.WHITE, "🔍");
        btnSearch.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnSearch);
        
        btnFilterByCourse = UIHelper.createStandardButton("Lọc Khóa", Color.WHITE, "📚");
        btnFilterByCourse.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnFilterByCourse);
        
        btnReload = UIHelper.createStandardButton("Tải lại", Color.WHITE, "⟳");
        btnReload.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnReload);
        
        add(pnlToolbar, BorderLayout.NORTH);

        // --- Form (WEST) ---
        JPanel pnlForm = UIHelper.createFormPanel("Thông tin lớp học");
        pnlForm.setPreferredSize(new Dimension(400, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFormField(pnlForm, "Tên lớp học: *", txtClassName = new JTextField(), gbc, row++);
        
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Khóa học: *"), gbc);
        gbc.gridx = 1;
        cmbCourse = new JComboBox<>();
        pnlForm.add(cmbCourse, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Giáo viên:"), gbc);
        gbc.gridx = 1;
        cmbTeacher = new JComboBox<>();
        pnlForm.add(cmbTeacher, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Phòng học:"), gbc);
        gbc.gridx = 1;
        cmbRoom = new JComboBox<>();
        pnlForm.add(cmbRoom, gbc);
        row++;

        addFormField(pnlForm, "Ngày bắt đầu:", txtStartDate = new JTextField(), gbc, row++);
        addFormField(pnlForm, "Ngày kết thúc:", txtEndDate = new JTextField(), gbc, row++);
        addFormField(pnlForm, "Sĩ số tối đa: *", txtMaxStudent = new JTextField(), gbc, row++);

        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(ClassStatus.values());
        pnlForm.add(cmbStatus, gbc);
        row++;

        // Buttons Panel
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
        String[] columns = {"ID", "Tên lớp", "Khóa học", "Giáo viên", "Phòng", "Bắt đầu", "Kết thúc", "Sĩ số", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblClass = new JTable(tableModel);
        setupTable(tblClass);
        add(new JScrollPane(tblClass), BorderLayout.CENTER);
    }

    private void addFormField(JPanel p, String label, JTextField tf, GridBagConstraints gbc, int r) {
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 1;
        p.add(createFieldLabel(label), gbc);
        gbc.gridx = 1;
        p.add(tf, gbc);
    }

    @Override
    protected void handleEvents() {
        btnSearch.addActionListener(e -> searchClasses());
        btnFilterByCourse.addActionListener(e -> filterByCourse());
        btnReload.addActionListener(e -> loadTableData());
        btnAdd.addActionListener(e -> addClass());
        btnUpdate.addActionListener(e -> updateClass());
        btnDelete.addActionListener(e -> deleteClass());
        btnClear.addActionListener(e -> clearForm());
        
        tblClass.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectClassFromTable();
        });
    }

    private void loadComboBoxData() {
        List<Course> courses = courseController.getActiveCourses();
        cmbCourse.removeAllItems();
        for (Course c : courses) cmbCourse.addItem(c);
        
        List<Teacher> teachers = teacherDAO.findAll();
        cmbTeacher.removeAllItems();
        cmbTeacher.addItem(null);
        for (Teacher t : teachers) cmbTeacher.addItem(t);
        
        List<Room> rooms = roomDAO.findAll();
        cmbRoom.removeAllItems();
        cmbRoom.addItem(null);
        for (Room r : rooms) cmbRoom.addItem(r);

        cmbTeacher.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "-- Chưa phân công --" : ((Teacher) value).getFullName());
                return this;
            }
        });
        
        cmbRoom.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "-- Chưa phân phòng --" : ((Room) value).getRoomName());
                return this;
            }
        });
    }

    private void addClass() {
        try {
            ClassEntity c = new ClassEntity();
            c.setClassName(txtClassName.getText().trim());
            c.setCourse((Course) cmbCourse.getSelectedItem());
            c.setTeacher((Teacher) cmbTeacher.getSelectedItem());
            c.setRoom((Room) cmbRoom.getSelectedItem());
            if (!txtStartDate.getText().isEmpty()) c.setStartDate(LocalDate.parse(txtStartDate.getText(), dateFormatter));
            if (!txtEndDate.getText().isEmpty()) c.setEndDate(LocalDate.parse(txtEndDate.getText(), dateFormatter));
            if (!txtMaxStudent.getText().isEmpty()) c.setMaxStudent(Integer.parseInt(txtMaxStudent.getText()));
            c.setStatus((ClassStatus) cmbStatus.getSelectedItem());
            
            JOptionPane.showMessageDialog(this, classController.createClass(c));
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void updateClass() {
        if (selectedClass == null) return;
        try {
            selectedClass.setClassName(txtClassName.getText().trim());
            selectedClass.setCourse((Course) cmbCourse.getSelectedItem());
            selectedClass.setTeacher((Teacher) cmbTeacher.getSelectedItem());
            selectedClass.setRoom((Room) cmbRoom.getSelectedItem());
            if (!txtStartDate.getText().isEmpty()) selectedClass.setStartDate(LocalDate.parse(txtStartDate.getText(), dateFormatter));
            if (!txtEndDate.getText().isEmpty()) selectedClass.setEndDate(LocalDate.parse(txtEndDate.getText(), dateFormatter));
            if (!txtMaxStudent.getText().isEmpty()) selectedClass.setMaxStudent(Integer.parseInt(txtMaxStudent.getText()));
            selectedClass.setStatus((ClassStatus) cmbStatus.getSelectedItem());
            
            JOptionPane.showMessageDialog(this, classController.updateClass(selectedClass));
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void deleteClass() {
        if (selectedClass == null) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa lớp " + selectedClass.getClassName() + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, classController.deleteClass(selectedClass.getClass_id().intValue()));
            loadTableData();
            clearForm();
        }
    }

    private void clearForm() {
        txtClassName.setText(""); txtStartDate.setText(""); txtEndDate.setText(""); txtMaxStudent.setText("");
        if (cmbCourse.getItemCount() > 0) cmbCourse.setSelectedIndex(0);
        cmbTeacher.setSelectedIndex(0); cmbRoom.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0); selectedClass = null;
        tblClass.clearSelection();
    }

    private void selectClassFromTable() {
        int row = tblClass.getSelectedRow();
        if (row >= 0) {
            Long id = (Long) tableModel.getValueAt(row, 0);
            selectedClass = classController.getClassById(id.intValue());
            if (selectedClass != null) {
                txtClassName.setText(selectedClass.getClassName());
                if (selectedClass.getCourse() != null) cmbCourse.setSelectedItem(selectedClass.getCourse());
                cmbTeacher.setSelectedItem(selectedClass.getTeacher());
                cmbRoom.setSelectedItem(selectedClass.getRoom());
                txtStartDate.setText(selectedClass.getStartDate() != null ? selectedClass.getStartDate().format(dateFormatter) : "");
                txtEndDate.setText(selectedClass.getEndDate() != null ? selectedClass.getEndDate().format(dateFormatter) : "");
                txtMaxStudent.setText(String.valueOf(selectedClass.getMaxStudent()));
                cmbStatus.setSelectedItem(selectedClass.getStatus());
            }
        }
    }

    private void searchClasses() {
        renderTable(classController.searchClasses(txtSearch.getText().trim()));
    }

    private void filterByCourse() {
        Course c = (Course) cmbCourse.getSelectedItem();
        if (c != null) renderTable(classController.getClassesByCourse(c));
    }

    @Override
    protected void loadTableData() {
        if (classController != null) renderTable(classController.getAllClasses());
    }

    private void renderTable(List<ClassEntity> classes) {
        tableModel.setRowCount(0);
        for (ClassEntity c : classes) {
            tableModel.addRow(new Object[]{
                c.getClass_id(), c.getClassName(),
                c.getCourse() != null ? c.getCourse().getCourseName() : "N/A",
                c.getTeacher() != null ? c.getTeacher().getFullName() : "Chưa có",
                c.getRoom() != null ? c.getRoom().getRoomName() : "Chưa có",
                c.getStartDate() != null ? c.getStartDate().format(dateFormatter) : "",
                c.getEndDate() != null ? c.getEndDate().format(dateFormatter) : "",
                c.getMaxStudent(), c.getStatus()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClassManagerFrame().setVisible(true));
    }
}
