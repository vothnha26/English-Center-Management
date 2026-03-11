package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.AttendanceController;
import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.academic.Attendance;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Enrollment;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.AttendanceStatus;
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
 * Quản lý điểm danh - Đã bổ sung Bảng tổng hợp chuyên cần.
 */
public class AttendanceManagerPanel extends BaseManagerPanel {
    private final AttendanceController attendanceController;
    private final ClassController classController;
    private final StudentController studentController;
    
    private JTabbedPane tabbedPane;
    private JComboBox<ClassEntity> cmbClass;
    private JTable tblChecklist, tblSummary;
    private DefaultTableModel checkModel, summaryModel;
    private JButton btnLoadClass, btnAllPresent, btnSaveAttendance;

    public AttendanceManagerPanel() {
        super();
        this.attendanceController = new AttendanceController();
        this.classController = new ClassController();
        this.studentController = new StudentController();
        loadClassData();
    }

    @Override
    protected void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIHelper.BOLD_FONT);

        tabbedPane.addTab("ĐIỂM DANH HÔM NAY", createChecklistTab());
        tabbedPane.addTab("TỔNG HỢP CHUYÊN CẦN", createSummaryTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createChecklistTab() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel pnlTop = new JPanel(new MigLayout("insets 20, fillx", "[][grow]20[]"));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(createFieldLabel("Lớp học:"));
        cmbClass = new JComboBox<>();
        pnlTop.add(cmbClass, "grow, height 35");
        btnLoadClass = UIHelper.createStandardButton("Tải lớp", UIHelper.ACCENT_COLOR, "");
        pnlTop.add(btnLoadClass, "height 35");
        p.add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"Có mặt", "ID", "Học viên", "Trạng thái", "Ghi chú"};
        checkModel = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int r, int c) { return c == 0 || c == 4; }
        };
        tblChecklist = new JTable(checkModel);
        setupTable(tblChecklist);
        tblChecklist.setRowHeight(40);
        p.add(new JScrollPane(tblChecklist), BorderLayout.CENTER);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlActions.setBackground(Color.WHITE);
        btnAllPresent = UIHelper.createStandardButton("TẤT CẢ CÓ MẶT", UIHelper.PRIMARY_COLOR, "");
        btnSaveAttendance = UIHelper.createStandardButton("LƯU ĐIỂM DANH", UIHelper.SUCCESS_COLOR, "");
        pnlActions.add(btnAllPresent);
        pnlActions.add(btnSaveAttendance);
        p.add(pnlActions, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createSummaryTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel("BÁO CÁO CHUYÊN CẦN THÁNG " + LocalDate.now().getMonthValue());
        lbl.setFont(UIHelper.TITLE_FONT); lbl.setForeground(UIHelper.PRIMARY_COLOR);
        p.add(lbl, BorderLayout.NORTH);

        String[] cols = {"Học viên", "Tổng buổi", "Vắng (K.Phép)", "Vắng (C.Phép)", "Tỷ lệ (%)"};
        summaryModel = new DefaultTableModel(cols, 0);
        tblSummary = new JTable(summaryModel);
        setupTable(tblSummary);
        p.add(new JScrollPane(tblSummary), BorderLayout.CENTER);

        return p;
    }

    private void loadClassData() {
        classController.getAllClasses().forEach(cmbClass::addItem);
    }

    @Override 
    protected void handleEvents() {
        btnLoadClass.addActionListener(e -> {
            ClassEntity clazz = (ClassEntity) cmbClass.getSelectedItem();
            if (clazz == null) return;
            
            // Lấy danh sách ghi danh từ StudentController
            List<Enrollment> enrollments = 
                studentController.getEnrollmentsByClass(clazz.getClass_id());
            
            checkModel.setRowCount(0);
            enrollments.forEach(enroll -> {
                checkModel.addRow(new Object[]{
                    true, // Mặc định có mặt
                    enroll.getStudent().getStudent_id(),
                    enroll.getStudent().getFullName(),
                    "Bình thường",
                    ""
                });
            });
            
            updateSummaryTab(clazz);
        });

        btnAllPresent.addActionListener(e -> {
            for (int i = 0; i < checkModel.getRowCount(); i++) {
                checkModel.setValueAt(true, i, 0);
            }
        });

        btnSaveAttendance.addActionListener(e -> {
            ClassEntity clazz = (ClassEntity) cmbClass.getSelectedItem();
            if (clazz == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học!");
                return;
            }

            int rowCount = checkModel.getRowCount();
            if (rowCount == 0) {
                JOptionPane.showMessageDialog(this, "Không có học viên để lưu!");
                return;
            }

            LocalDate today = LocalDate.now();
            StringBuilder resultMsg = new StringBuilder();
            
            for (int i = 0; i < rowCount; i++) {
                boolean isPresent = (boolean) checkModel.getValueAt(i, 0);
                Long studentId = (Long) checkModel.getValueAt(i, 1);
                String note = (String) checkModel.getValueAt(i, 4);
                
                Student student = studentController.getStudentById(studentId);
                
                Attendance att = new Attendance();
                att.setClazz(clazz);
                att.setStudent(student);
                att.setAttend_date(today);
                att.setStatus(isPresent ? AttendanceStatus.Present : AttendanceStatus.Absent);
                att.setNote(note);
                
                String res = attendanceController.createAttendance(att);
                if (res.contains("Lỗi")) {
                    // Nếu đã tồn tại, ta thử cập nhật
                    if (res.contains("đã được điểm danh")) {
                        // Thử tìm bản ghi cũ để cập nhật
                        List<Attendance> existingList = 
                            attendanceController.getAttendancesByClassAndDate(clazz, today);
                        
                        existingList.stream()
                            .filter(a -> a.getStudent().getStudent_id().equals(studentId))
                            .findFirst()
                            .ifPresent(oldAtt -> {
                                oldAtt.setStatus(isPresent ? AttendanceStatus.Present : AttendanceStatus.Absent);
                                oldAtt.setNote(note);
                                attendanceController.updateAttendance(oldAtt);
                            });
                    } else {
                        resultMsg.append(res).append("\n");
                    }
                }
            }
            
            if (resultMsg.length() == 0) {
                JOptionPane.showMessageDialog(this, "Lưu điểm danh thành công!");
                updateSummaryTab(clazz);
            } else {
                JOptionPane.showMessageDialog(this, "Có một số lỗi xảy ra:\n" + resultMsg.toString());
            }
        });
    }

    private void updateSummaryTab(ClassEntity clazz) {
        summaryModel.setRowCount(0);
        List<Enrollment> enrollments = 
                studentController.getEnrollmentsByClass(clazz.getClass_id());
        
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayOfMonth = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1);

        enrollments.forEach(enroll -> {
            Student s = enroll.getStudent();
            long absent = attendanceController.countAbsentByStudentAndClass(s, clazz);
            double rate = attendanceController.getAttendanceRate(clazz, firstDayOfMonth, lastDayOfMonth);
            
            summaryModel.addRow(new Object[]{
                s.getFullName(),
                "12", // Tổng buổi (dummy)
                absent,
                "0",
                String.format("%.1f%%", 100 - (absent * 100.0 / 12)) // Giả sử 12 buổi/tháng
            });
        });
    }

    @Override protected void loadTableData() {}
}
