package com.trungtamdaotao.view.academic;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.trungtamdaotao.controller.academic.AttendanceController;
import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.model.dao.student.impl.StudentDAOImpl;
import com.trungtamdaotao.model.dao.student.IStudentDAO;
import com.trungtamdaotao.model.entity.academic.Attendance;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.AttendanceStatus;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.util.security.IPermission;
import com.trungtamdaotao.util.security.UserSession;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AttendanceManagerFrame extends BaseManagerFrame {
    private final AttendanceController attendanceController;
    private final ClassController classController;
    private final IStudentDAO studentDAO;
    
    private JTable tblAttendance;
    private DefaultTableModel tableModel;
    
    // Form fields
    private DatePicker dpAttendDate;
    private JTextField txtNote;
    private JTextField txtSearchDate;
    private JComboBox<Student> cmbStudent;
    private JComboBox<ClassEntity> cmbClass;
    private JComboBox<AttendanceStatus> cmbStatus;
    
    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JButton btnSearchByDate, btnSearchByClass, btnReload, btnStatistics;
    
    // Selected attendance for update/delete
    private Attendance selectedAttendance;

    public AttendanceManagerFrame() {
        super("Quản lý Điểm danh", 
              new AccountRole[]{AccountRole.ADMIN, AccountRole.STAFF}, 
              new StaffRole[]{StaffRole.MANAGER, StaffRole.CONSULTANT});
        this.attendanceController = new AttendanceController();
        this.classController = new ClassController();
        this.studentDAO = new StudentDAOImpl();
        
        loadComboBoxData();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        // --- Toolbar (NORTH) ---
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlToolbar.setBackground(UIHelper.PRIMARY_COLOR);
        
        JLabel lblSearch = new JLabel("Ngày:");
        lblSearch.setForeground(Color.WHITE);
        lblSearch.setFont(UIHelper.BOLD_FONT);
        pnlToolbar.add(lblSearch);
        
        txtSearchDate = new JTextField(10);
        pnlToolbar.add(txtSearchDate);
        
        btnSearchByDate = UIHelper.createStandardButton("Tìm", Color.WHITE, "🔍");
        btnSearchByDate.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnSearchByDate);
        
        pnlToolbar.add(new JSeparator(SwingConstants.VERTICAL));
        
        btnSearchByClass = UIHelper.createStandardButton("Lọc Lớp", Color.WHITE, "📚");
        btnSearchByClass.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnSearchByClass);
        
        btnStatistics = UIHelper.createStandardButton("Thống kê", Color.WHITE, "📈");
        btnStatistics.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnStatistics);
        
        btnReload = UIHelper.createStandardButton("Tải lại", Color.WHITE, "⟳");
        btnReload.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnReload);
        
        add(pnlToolbar, BorderLayout.NORTH);

        // --- Form (WEST) ---
        JPanel pnlForm = UIHelper.createFormPanel("Thông tin điểm danh");
        pnlForm.setPreferredSize(new Dimension(420, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Học viên: *"), gbc);
        gbc.gridx = 1;
        cmbStudent = new JComboBox<>();
        pnlForm.add(cmbStudent, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Lớp học: *"), gbc);
        gbc.gridx = 1;
        cmbClass = new JComboBox<>();
        pnlForm.add(cmbClass, gbc);
        row++;

        // DatePicker cho Ngày điểm danh
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Ngày: *"), gbc);
        gbc.gridx = 1;
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dpAttendDate = new DatePicker(dateSettings);
        pnlForm.add(dpAttendDate, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Trạng thái: *"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(AttendanceStatus.values());
        pnlForm.add(cmbStatus, gbc);
        row++;

        addFormField(pnlForm, "Ghi chú:", txtNote = new JTextField(), gbc, row++);

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
        String[] columns = {"ID", "Học viên", "Lớp", "Khóa", "Ngày", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAttendance = new JTable(tableModel);
        setupTable(tblAttendance);
        add(new JScrollPane(tblAttendance), BorderLayout.CENTER);
    }

    private void addFormField(JPanel p, String label, JTextField tf, GridBagConstraints gbc, int r) {
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 1;
        p.add(createFieldLabel(label), gbc);
        gbc.gridx = 1;
        p.add(tf, gbc);
    }

    @Override
    protected void handleEvents() {
        btnSearchByDate.addActionListener(e -> searchByDate());
        btnSearchByClass.addActionListener(e -> filterByClass());
        btnStatistics.addActionListener(e -> showStatistics());
        btnReload.addActionListener(e -> loadTableData());
        btnAdd.addActionListener(e -> addAttendance());
        btnUpdate.addActionListener(e -> updateAttendance());
        if (UserSession.getPermissions().canDelete()) {
            btnDelete.addActionListener(e -> deleteAttendance());
        }
        btnClear.addActionListener(e -> clearForm());
        
        tblAttendance.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectAttendanceFromTable();
        });
    }

    private void loadComboBoxData() {
        List<Student> students = studentDAO.findAll();
        cmbStudent.removeAllItems();
        for (Student s : students) cmbStudent.addItem(s);
        
        cmbStudent.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    Student s = (Student) value;
                    setText(s.getFullName() + " (" + (s.getPhone() != null ? s.getPhone() : "N/A") + ")");
                }
                return this;
            }
        });
        
        List<ClassEntity> classes = classController.getAllClasses();
        cmbClass.removeAllItems();
        for (ClassEntity c : classes) cmbClass.addItem(c);
        
        cmbClass.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ClassEntity) {
                    ClassEntity c = (ClassEntity) value;
                    setText(c.getClassName() + " (" + (c.getCourse() != null ? c.getCourse().getCourseName() : "N/A") + ")");
                }
                return this;
            }
        });
    }

    private void addAttendance() {
        try {
            Attendance a = new Attendance();
            a.setStudent((Student) cmbStudent.getSelectedItem());
            a.setClazz((ClassEntity) cmbClass.getSelectedItem());
            a.setAttend_date(dpAttendDate.getDate());
            a.setStatus((AttendanceStatus) cmbStatus.getSelectedItem());
            a.setNote(txtNote.getText().trim());
            
            JOptionPane.showMessageDialog(this, attendanceController.createAttendance(a));
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void updateAttendance() {
        if (selectedAttendance == null) return;
        try {
            selectedAttendance.setStudent((Student) cmbStudent.getSelectedItem());
            selectedAttendance.setClazz((ClassEntity) cmbClass.getSelectedItem());
            selectedAttendance.setAttend_date(dpAttendDate.getDate());
            selectedAttendance.setStatus((AttendanceStatus) cmbStatus.getSelectedItem());
            selectedAttendance.setNote(txtNote.getText().trim());
            
            JOptionPane.showMessageDialog(this, attendanceController.updateAttendance(selectedAttendance));
            loadTableData();
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void deleteAttendance() {
        if (selectedAttendance == null) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa điểm danh này?");
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, attendanceController.deleteAttendance(selectedAttendance.getAttendance_id().intValue()));
            loadTableData();
            clearForm();
        }
    }

    private void clearForm() {
        dpAttendDate.clear(); txtNote.setText("");
        if (cmbStudent.getItemCount() > 0) cmbStudent.setSelectedIndex(0);
        if (cmbClass.getItemCount() > 0) cmbClass.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0); selectedAttendance = null;
        tblAttendance.clearSelection();
    }

    private void selectAttendanceFromTable() {
        int row = tblAttendance.getSelectedRow();
        if (row >= 0) {
            Long id = (Long) tableModel.getValueAt(row, 0);
            selectedAttendance = attendanceController.getAttendanceById(id.intValue());
            if (selectedAttendance != null) {
                cmbStudent.setSelectedItem(selectedAttendance.getStudent());
                cmbClass.setSelectedItem(selectedAttendance.getClazz());
                dpAttendDate.setDate(selectedAttendance.getAttend_date());
                cmbStatus.setSelectedItem(selectedAttendance.getStatus());
                txtNote.setText(selectedAttendance.getNote() != null ? selectedAttendance.getNote() : "");
            }
        }
    }

    private void searchByDate() {
        try {
            LocalDate date = LocalDate.parse(txtSearchDate.getText().trim());
            renderTable(attendanceController.getAttendancesByDate(date));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ (yyyy-MM-dd)");
        }
    }

    private void filterByClass() {
        ClassEntity c = (ClassEntity) cmbClass.getSelectedItem();
        if (c != null) renderTable(attendanceController.getAttendancesByClass(c));
    }

    private void showStatistics() {
        ClassEntity c = (ClassEntity) cmbClass.getSelectedItem();
        if (c == null) return;
        String start = JOptionPane.showInputDialog(this, "Từ ngày (yyyy-MM-dd):", LocalDate.now().minusMonths(1).toString());
        String end = JOptionPane.showInputDialog(this, "Đến ngày (yyyy-MM-dd):", LocalDate.now().toString());
        if (start != null && end != null) {
            try {
                double rate = attendanceController.getAttendanceRate(c, LocalDate.parse(start), LocalDate.parse(end));
                JOptionPane.showMessageDialog(this, String.format("Tỷ lệ có mặt lớp %s: %.2f%%", c.getClassName(), rate));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày!");
            }
        }
    }

    @Override
    protected void loadTableData() {
        if (attendanceController != null) renderTable(attendanceController.getAllAttendances());
    }

    private void renderTable(List<Attendance> list) {
        tableModel.setRowCount(0);
        for (Attendance a : list) {
            tableModel.addRow(new Object[]{
                a.getAttendance_id(),
                a.getStudent() != null ? a.getStudent().getFullName() : "N/A",
                a.getClazz() != null ? a.getClazz().getClassName() : "N/A",
                a.getClazz() != null && a.getClazz().getCourse() != null ? a.getClazz().getCourse().getCourseName() : "N/A",
                a.getAttend_date(),
                a.getStatus(), a.getNote()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AttendanceManagerFrame().setVisible(true));
    }
}
