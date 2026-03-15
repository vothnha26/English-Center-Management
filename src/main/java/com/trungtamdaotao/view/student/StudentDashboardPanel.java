package com.trungtamdaotao.view.student;

import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Enrollment;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.util.security.UserSession;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Method;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Student landing page using a left navigation panel.
 */
public class StudentDashboardPanel extends BaseManagerPanel {
    private final ClassController classController = new ClassController();
    private final StudentController studentController = new StudentController();

    // Panels
    private JPanel pnlCourses;
    private JPanel pnlProfile;
    private JPanel pnlTimetable;
    private JPanel pnlGrades;

    // Courses components
    private JTable tblCourses;
    private DefaultTableModel coursesModel;
    private JButton btnRequest;

    public StudentDashboardPanel() { super(); }

    @Override protected void initComponents() {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("TRANG HỌC VIÊN");
        title.setFont(UIHelper.TITLE_FONT);
        title.setForeground(UIHelper.PRIMARY_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(title, BorderLayout.NORTH);

        // Initialize subpanels (no internal navigation)
        pnlCourses = createCoursesPanel();
        pnlProfile = createProfilePanel();
        pnlTimetable = createTimetablePanel();
        pnlGrades = createGradesPanel();

        // Default selection handled by main sidebar
    }

    @Override protected void handleEvents() {}

    @Override protected void loadTableData() {
        SwingUtilities.invokeLater(() -> {
            refreshCourses();
            refreshProfile();
            refreshTimetable();
            refreshGrades();
        });
    }

    // --- Courses ---
    private JPanel createCoursesPanel() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(Color.WHITE);
        String[] cols = {"ID","Khóa","Lớp","Bắt đầu","Số lượng","Trạng thái"};
        coursesModel = new DefaultTableModel(cols,0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tblCourses = new JTable(coursesModel);
        setupTable(tblCourses);
        p.add(new JScrollPane(tblCourses), BorderLayout.CENTER);

        btnRequest = UIHelper.createStandardButton("Yêu cầu ghi danh", UIHelper.PRIMARY_COLOR, "");
        btnRequest.addActionListener(e -> doRequestFromCourses());
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bot.setOpaque(false); bot.add(btnRequest);
        p.add(bot, BorderLayout.SOUTH);

        tblCourses.getSelectionModel().addListSelectionListener(e -> updateRequestButtonState());

        return p;
    }

    private void refreshCourses() {
        coursesModel.setRowCount(0);
        List<ClassEntity> classes = classController.getAllClasses();
        Student student = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getStudent() : null;
        List<Enrollment> studentEnrolls = student != null ? studentController.getEnrollmentsByStudent(student.getStudent_id()) : null;

        for (ClassEntity c : classes) {
            String courseName = c.getCourse() != null ? c.getCourse().getCourseName() : "-";
            String statusForMe = "-";
            if (studentEnrolls != null) {
                for (Enrollment en : studentEnrolls) {
                    try {
                        if (en.getClazz() != null && en.getClazz().getClass_id().equals(c.getClass_id())) {
                            String sname = en.getStatus() != null ? en.getStatus().name().toLowerCase() : "";
                            if (sname.contains("pend")) statusForMe = "Chờ phê duyệt";
                            else if (sname.contains("enrol") || sname.contains("approved") || sname.contains("accept")) statusForMe = "Đã phê duyệt";
                            else statusForMe = sname;
                            break;
                        }
                    } catch (Exception ignored) {}
                }
            }
            coursesModel.addRow(new Object[]{c.getClass_id(), courseName, c.getClassName(), c.getStartDate(), c.getMaxStudent(), statusForMe});
        }
        updateRequestButtonState();
    }

    private void updateRequestButtonState() {
        int r = tblCourses.getSelectedRow();
        if (r < 0) { btnRequest.setEnabled(false); return; }
        String status = (String) coursesModel.getValueAt(r, 5);
        // Disabled if already requested or approved
        if (status != null && (status.contains("Chờ") || status.contains("Đã"))) { btnRequest.setEnabled(false); return; }

        // If class has end date and not finished -> disable request until finished
        Long classId = (Long) coursesModel.getValueAt(r, 0);
        ClassEntity c = classController.getClassById(classId.intValue());
        boolean finished = false;
        try {
            Method m = c.getClass().getMethod("getEndDate");
            Object end = m.invoke(c);
            if (end instanceof Date) {
                finished = ((Date) end).before(new Date());
            } else if (end instanceof LocalDate) {
                finished = ((LocalDate) end).isBefore(LocalDate.now());
            }
        } catch (NoSuchMethodException ex) {
            // No end date field; default to allow request
            finished = true;
        } catch (Exception ignored) { finished = true; }

        btnRequest.setEnabled(finished);
        if (!finished) btnRequest.setText("Chưa thể yêu cầu (chưa kết thúc)");
        else btnRequest.setText("Yêu cầu ghi danh");
    }

    private void doRequestFromCourses() {
        int r = tblCourses.getSelectedRow();
        if (r < 0) { UIHelper.showError(this, "Vui lòng chọn lớp để đăng ký."); return; }
        Long classId = (Long) coursesModel.getValueAt(r, 0);
        ClassEntity selected = classController.getClassById(classId.intValue());
        if (selected == null) { UIHelper.showError(this, "Không tìm thấy lớp."); return; }
        try {
            Student student = UserSession.getCurrentUser().getStudent();
            if (student == null) { UIHelper.showError(this, "Bạn chưa liên kết tài khoản với hồ sơ học viên."); return; }
            studentController.enroll(student, selected);
            UIHelper.showInfo(this, "Yêu cầu ghi danh đã được gửi (đợi phê duyệt).");
            refreshCourses();
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    // --- Profile ---
    private JLabel lblProfile;
    private JPanel createProfilePanel() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(Color.WHITE);
        lblProfile = new JLabel(); lblProfile.setFont(UIHelper.MAIN_FONT);
        p.add(lblProfile, BorderLayout.NORTH);
        return p;
    }

    private void refreshProfile() {
        var user = UserSession.getCurrentUser();
        if (user == null || user.getStudent() == null) {
            lblProfile.setText("Chưa liên kết hồ sơ học viên.");
            return;
        }
        Student s = user.getStudent();
        StringBuilder sb = new StringBuilder();
        sb.append("Họ tên: ").append(s.getFullName()).append("<br/>");
        sb.append("Email: ").append(s.getEmail()).append("<br/>");
        sb.append("SĐT: ").append(s.getPhone()).append("<br/>");
        sb.append("Ngày sinh: ").append(s.getDateOfBirth()).append("<br/>");
        lblProfile.setText("<html>" + sb.toString() + "</html>");
    }

    // --- Timetable ---
    private JTable tblTimetable;
    private DefaultTableModel ttModel;
    private JPanel createTimetablePanel() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(Color.WHITE);
        String[] cols = {"Thứ","Giờ","Khóa","Lớp","Phòng"};
        ttModel = new DefaultTableModel(cols,0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tblTimetable = new JTable(ttModel);
        p.add(new JScrollPane(tblTimetable), BorderLayout.CENTER);
        return p;
    }

    private void refreshTimetable() {
        ttModel.setRowCount(0);
        Student s = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getStudent() : null;
        if (s == null) return;
        List<Enrollment> enrolls = studentController.getEnrollmentsByStudent(s.getStudent_id());
        if (enrolls == null) return;
        for (Enrollment e : enrolls) {
            try {
                String day = "TBD";
                String time = LocalTime.of(9,0).toString();
                String course = e.getClazz() != null && e.getClazz().getCourse() != null ? e.getClazz().getCourse().getCourseName() : "-";
                String cls = e.getClazz() != null ? e.getClazz().getClassName() : "-";
                String room = e.getClazz() != null && e.getClazz().getRoom() != null ? e.getClazz().getRoom().getRoomName() : "-";
                ttModel.addRow(new Object[]{day, time, course, cls, room});
            } catch (Exception ignore) {}
        }
    }

    // --- Grades ---
    private JTable tblGrades;
    private DefaultTableModel gradesModel;
    private JPanel createGradesPanel() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(Color.WHITE);
        String[] cols = {"Khóa","Lớp","Tên bài","Điểm"};
        gradesModel = new DefaultTableModel(cols,0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tblGrades = new JTable(gradesModel);
        p.add(new JScrollPane(tblGrades), BorderLayout.CENTER);
        return p;
    }

    private void refreshGrades() {
        gradesModel.setRowCount(0);
        // no grade service available yet
    }

    // Public getters so MainMenuFrame can add student subpanels to the global content card layout
    public JPanel getCoursesPanel() { return pnlCourses; }
    public JPanel getProfilePanel() { return pnlProfile; }
    public JPanel getTimetablePanel() { return pnlTimetable; }
    public JPanel getGradesPanel() { return pnlGrades; }
}
