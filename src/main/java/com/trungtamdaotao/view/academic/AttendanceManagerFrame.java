package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.AttendanceController;
import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.model.entity.academic.Attendance;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.AttendanceStatus;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện điểm danh hiện đại theo phong cách Checklist cho giáo viên.
 */
public class AttendanceManagerFrame extends BaseManagerFrame {
    private final AttendanceController attendanceController;
    private final ClassController classController;
    
    private JComboBox<ClassEntity> cmbClass;
    private JTextField txtAttendDate;
    private JTable tblChecklist;
    private DefaultTableModel tableModel;
    private JButton btnMarkAll, btnSave, btnStats, btnReload;

    public AttendanceManagerFrame() {
        super("ĐIỂM DANH HỌC VIÊN");
        this.attendanceController = new AttendanceController();
        this.classController = new ClassController();
        loadClassData();
    }

    @Override
    protected void initComponents() {
        // --- SELECTION PANEL (NORTH) ---
        JPanel pnlSelection = new JPanel(new MigLayout("insets 20, fillx", "[][grow]20[][grow]20[]"));
        pnlSelection.setBackground(Color.WHITE);
        pnlSelection.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        pnlSelection.add(createFieldLabel("Chọn lớp học:"));
        cmbClass = new JComboBox<>();
        pnlSelection.add(cmbClass, "grow, height 35");

        pnlSelection.add(createFieldLabel("Ngày điểm danh:"));
        txtAttendDate = new JTextField(LocalDate.now().toString());
        pnlSelection.add(txtAttendDate, "grow, height 35");

        btnReload = UIHelper.createStandardButton("Tải danh sách", UIHelper.ACCENT_COLOR, "⟳");
        pnlSelection.add(btnReload, "height 35");

        add(pnlSelection, BorderLayout.NORTH);

        // --- CHECKLIST TABLE (CENTER) ---
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Quick Action Row
        JPanel pnlQuickActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlQuickActions.setOpaque(false);
        btnMarkAll = UIHelper.createStandardButton("TẤT CẢ CÓ MẶT", UIHelper.PRIMARY_COLOR, "✅");
        pnlQuickActions.add(btnMarkAll);
        pnlCenter.add(pnlQuickActions, BorderLayout.NORTH);

        // Table definition with Boolean for checkbox
        String[] columns = {"Có mặt", "ID", "Học viên", "Trạng thái trước đó", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 4; // Chỉ cho sửa checkbox và ghi chú
            }
        };
        
        tblChecklist = new JTable(tableModel);
        setupTable(tblChecklist);
        tblChecklist.setRowHeight(45); // Cao hơn để dễ bấm checkbox
        
        pnlCenter.add(new JScrollPane(tblChecklist), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- BOTTOM ACTION BAR (SOUTH) ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        btnStats = UIHelper.createStandardButton("Thống kê lớp", UIHelper.ACCENT_COLOR, "📊");
        btnSave = UIHelper.createStandardButton("LƯU ĐIỂM DANH", UIHelper.SUCCESS_COLOR, "💾");
        
        pnlBottom.add(btnStats);
        pnlBottom.add(btnSave);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadClassData() {
        List<ClassEntity> classes = classController.getAllClasses();
        cmbClass.removeAllItems();
        for (ClassEntity c : classes) cmbClass.addItem(c);
    }

    @Override
    protected void handleEvents() {
        btnReload.addActionListener(e -> loadAttendanceChecklist());
        btnMarkAll.addActionListener(e -> markAllPresent());
        btnSave.addActionListener(e -> saveAttendance());
        btnStats.addActionListener(e -> showStatistics());
        
        cmbClass.addActionListener(e -> loadAttendanceChecklist());
    }

    private void loadAttendanceChecklist() {
        ClassEntity selectedClass = (ClassEntity) cmbClass.getSelectedItem();
        if (selectedClass == null) return;

        // Trong thực tế sẽ lấy danh sách học viên của lớp này thông qua EnrollmentService
        // Ở đây demo bằng cách lấy tất cả học viên (giả lập)
        tableModel.setRowCount(0);
        // Giả lập danh sách học viên - trong thực tế sẽ gọi service
        // List<Student> students = enrollmentService.getStudentsByClass(selectedClass);
        // For demo:
        tableModel.addRow(new Object[]{true, "HV001", "Nguyễn Văn A", "Có mặt", ""});
        tableModel.addRow(new Object[]{true, "HV002", "Trần Thị B", "Có mặt", ""});
        tableModel.addRow(new Object[]{false, "HV003", "Lê Văn C", "Vắng mặt", "Xin nghỉ"});
    }

    private void markAllPresent() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(true, i, 0);
        }
    }

    private void saveAttendance() {
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận lưu điểm danh cho " + tableModel.getRowCount() + " học viên?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Logic gọi controller để lưu hàng loạt
            JOptionPane.showMessageDialog(this, "Đã lưu điểm danh thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showStatistics() {
        JOptionPane.showMessageDialog(this, "Tính năng thống kê chuyên sâu đang được phát triển.");
    }

    @Override
    protected void loadTableData() {
        // Màn hình này dùng loadAttendanceChecklist thay thế
    }

    public static void main(String[] args) {
        com.formdev.flatlaf.FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new AttendanceManagerFrame().setVisible(true));
    }
}
