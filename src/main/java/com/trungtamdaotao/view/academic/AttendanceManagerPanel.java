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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttendanceManagerPanel extends BaseManagerPanel {
    private AttendanceController attendanceController;
    private ClassController classController;
    private StudentController studentController;

    private JTabbedPane tabbedPane;

    // Checklist tab
    private JComboBox<ClassEntity> cmbClass;
    private JTable tblChecklist;
    private DefaultTableModel checkModel;
    private JButton btnLoadClass, btnAllPresent, btnSaveAttendance;

    // Summary tab
    private JComboBox<ClassEntity> cmbSummaryClass;
    private JSpinner spnMonth, spnYear;
    private JTable tblSummary;
    private DefaultTableModel summaryModel;
    private JButton btnLoadSummary;

    public AttendanceManagerPanel() {
        super();
        loadClassData();
    }

    private void ensureControllersInitialized() {
        if (attendanceController == null) {
            attendanceController = new AttendanceController();
        }
        if (classController == null) {
            classController = new ClassController();
        }
        if (studentController == null) {
            studentController = new StudentController();
        }
    }

    @Override
    protected void initComponents() {
        ensureControllersInitialized();
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIHelper.BOLD_FONT);

        tabbedPane.addTab("ĐIỂM DANH HÔM NAY", createChecklistTab());
        tabbedPane.addTab("TỔNG HỢP CHUYÊN CẦN", createSummaryTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createChecklistTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        // Toolbar
        JPanel pnlTop = new JPanel(new MigLayout("insets 15 20 15 20, fillx", "[][grow]15[]30[]"));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(createFieldLabel("Lớp học:"));
        cmbClass = new JComboBox<>();
        pnlTop.add(cmbClass, "grow, height 35");
        btnLoadClass = UIHelper.createStandardButton("Tải lớp", UIHelper.ACCENT_COLOR, "");
        pnlTop.add(btnLoadClass, "height 35");
        JLabel lblDate = new JLabel("Ngày: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblDate.setFont(UIHelper.MAIN_FONT);
        lblDate.setForeground(new Color(100, 100, 100));
        pnlTop.add(lblDate);
        p.add(pnlTop, BorderLayout.NORTH);

        // Table — 4 columns (removed useless "Trạng thái")
        String[] cols = { "Điểm danh", "ID", "Học viên", "Ghi chú" };
        checkModel = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? AttendanceStatus.class : String.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 0 || c == 3;
            }
        };
        tblChecklist = new JTable(checkModel);
        setupTable(tblChecklist);
        tblChecklist.setRowHeight(40);
        tblChecklist.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblChecklist.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblChecklist.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblChecklist.getColumnModel().getColumn(3).setPreferredWidth(300);
        JComboBox<AttendanceStatus> statusCombo = new JComboBox<>(
                new AttendanceStatus[] { AttendanceStatus.Present, AttendanceStatus.Absent, AttendanceStatus.Late });
        tblChecklist.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(statusCombo));
        p.add(new JScrollPane(tblChecklist), BorderLayout.CENTER);

        // Actions
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
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        // Header + filter
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel lbl = new JLabel("BÁO CÁO CHUYÊN CẦN");
        lbl.setFont(UIHelper.TITLE_FONT);
        lbl.setForeground(UIHelper.PRIMARY_COLOR);
        pnlHeader.add(lbl, BorderLayout.NORTH);

        JPanel pnlFilter = new JPanel(new MigLayout("insets 10 0 5 0, fillx", "[][grow]20[][55!]10[][70!]20[]"));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.add(createFieldLabel("Lớp học:"));
        cmbSummaryClass = new JComboBox<>();
        classController.getAllClasses().forEach(cmbSummaryClass::addItem);
        pnlFilter.add(cmbSummaryClass, "grow, height 35");
        pnlFilter.add(createFieldLabel("Tháng:"));
        spnMonth = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        pnlFilter.add(spnMonth, "height 35");
        pnlFilter.add(createFieldLabel("Năm:"));
        spnYear = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1));
        ((JSpinner.NumberEditor) spnYear.getEditor()).getFormat().setGroupingUsed(false);
        pnlFilter.add(spnYear, "height 35");
        btnLoadSummary = UIHelper.createStandardButton("Xem báo cáo", UIHelper.ACCENT_COLOR, "");
        pnlFilter.add(btnLoadSummary, "height 35");
        pnlHeader.add(pnlFilter, BorderLayout.CENTER);
        p.add(pnlHeader, BorderLayout.NORTH);

        // Table with full columns
        String[] cols = { "Học viên", "Tổng buổi", "Có mặt", "Đi trễ", "Vắng", "Tỷ lệ (%)" };
        summaryModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblSummary = new JTable(summaryModel);
        setupTable(tblSummary);
        tblSummary.setRowHeight(38);
        tblSummary.getColumnModel().getColumn(0).setPreferredWidth(180);

        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(new EmptyBorder(0, 20, 20, 20));
        pnlTable.setBackground(Color.WHITE);
        pnlTable.add(new JScrollPane(tblSummary), BorderLayout.CENTER);
        p.add(pnlTable, BorderLayout.CENTER);

        return p;
    }

    private void loadClassData() {
        classController.getAllClasses().forEach(cmbClass::addItem);
    }

    @Override
    protected void handleEvents() {
        ensureControllersInitialized();
        btnLoadClass.addActionListener(e -> {
            ClassEntity clazz = (ClassEntity) cmbClass.getSelectedItem();
            if (clazz == null)
                return;

            // Lấy danh sách ghi danh từ StudentController
            List<Enrollment> enrollments = studentController.getEnrollmentsByClass(clazz.getClass_id());

            checkModel.setRowCount(0);
            enrollments.forEach(enroll -> {
                checkModel.addRow(new Object[] {
                        AttendanceStatus.Present,
                        enroll.getStudent().getStudent_id(),
                        enroll.getStudent().getFullName(),
                        ""
                });
            });

        });

        btnAllPresent.addActionListener(e -> {
            for (int i = 0; i < checkModel.getRowCount(); i++) {
                checkModel.setValueAt(AttendanceStatus.Present, i, 0);
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
                AttendanceStatus status = (AttendanceStatus) checkModel.getValueAt(i, 0);
                Long studentId = (Long) checkModel.getValueAt(i, 1);
                String note = (String) checkModel.getValueAt(i, 3);

                Student student = studentController.getStudentById(studentId);

                Attendance att = new Attendance();
                att.setClazz(clazz);
                att.setStudent(student);
                att.setAttend_date(today);
                att.setStatus(status);
                att.setNote(note);

                String res = attendanceController.createAttendance(att);
                if (res.contains("Lỗi")) {
                    // Nếu đã tồn tại, ta thử cập nhật
                    if (res.contains("đã được điểm danh")) {
                        // Thử tìm bản ghi cũ để cập nhật
                        List<Attendance> existingList = attendanceController.getAttendancesByClassAndDate(clazz, today);

                        existingList.stream()
                                .filter(a -> a.getStudent().getStudent_id().equals(studentId))
                                .findFirst()
                                .ifPresent(oldAtt -> {
                                    oldAtt.setStatus(status);
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

            } else {
                JOptionPane.showMessageDialog(this, "Có một số lỗi xảy ra:\n" + resultMsg.toString());
            }
        });
        btnLoadSummary.addActionListener(e -> {
            ClassEntity clazzSelection = (ClassEntity) cmbSummaryClass.getSelectedItem();
            if (clazzSelection == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học!");
                return;
            }
            loadSummaryData(clazzSelection);
        });
    }

    private void loadSummaryData(ClassEntity clazz) {
        summaryModel.setRowCount(0);
        int month = (int) spnMonth.getValue();
        int year = (int) spnYear.getValue();
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        List<Attendance> all = attendanceController.getAttendancesByClass(clazz);
        Map<Long, List<Attendance>> byStudent = all.stream()
                .filter(a -> a.getAttend_date() != null
                        && !a.getAttend_date().isBefore(firstDay)
                        && !a.getAttend_date().isAfter(lastDay))
                .collect(Collectors.groupingBy(a -> a.getStudent().getStudent_id()));

        studentController.getEnrollmentsByClass(clazz.getClass_id()).forEach(enroll -> {
            Student s = enroll.getStudent();
            List<Attendance> records = byStudent.getOrDefault(s.getStudent_id(), List.of());
            long total = records.size();
            long present = records.stream().filter(a -> a.getStatus() == AttendanceStatus.Present).count();
            long late = records.stream().filter(a -> a.getStatus() == AttendanceStatus.Late).count();
            long absent = records.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.Absent
                            || a.getStatus() == AttendanceStatus.Excused_Absent)
                    .count();
            double rate = total > 0 ? (present + late) * 100.0 / total : 0.0;
            summaryModel.addRow(new Object[] {
                    s.getFullName(), total, present, late, absent,
                    String.format("%.1f%%", rate)
            });
        });
    }

    @Override
    protected void loadTableData() {
    }
}
