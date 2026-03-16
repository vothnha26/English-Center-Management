package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.dao.student.impl.EnrollmentDAOImpl;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Enrollment;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.service.common.EmailFeedbackService;
import com.trungtamdaotao.model.service.common.PdfExportService;
import com.trungtamdaotao.model.service.student.EnrollmentService;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý điểm số tích hợp Gửi Email và Xuất PDF phiếu điểm.
 */
public class GradeManagerPanel extends BaseManagerPanel {
    private final ClassController classController;
    private final EnrollmentService enrollmentService;
    private final EmailFeedbackService emailService;
    private final PdfExportService pdfService;

    private JComboBox<ClassEntity> cmbClass;
    private JTable tblGrade;
    private DefaultTableModel tableModel;
    private JButton btnSaveTemp, btnFinalLock, btnSendEmail, btnExportPdf;

    // Lưu trữ danh sách student hiện tại để lấy email
    private List<Student> currentStudents = new ArrayList<>();

    public GradeManagerPanel() {
        super();
        this.classController = new ClassController();
        this.studentController = new StudentController();
        this.enrollmentService = new EnrollmentService(new EnrollmentDAOImpl());
        this.emailService = new EmailFeedbackService();
        this.pdfService = new PdfExportService();
        loadClassData();
    }

    @Override
    protected void initComponents() {
        setLayout(new BorderLayout());

        // --- TOP SELECTION ---
        JPanel pnlTop = new JPanel(new MigLayout("insets 20, fillx", "[][grow]20[]"));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(createFieldLabel("Chọn lớp học:"));
        cmbClass = new JComboBox<>();
        pnlTop.add(cmbClass, "grow, height 35");

        JButton btnLoad = UIHelper.createStandardButton("Tải danh sách", UIHelper.ACCENT_COLOR, "🔍");
        pnlTop.add(btnLoad, "height 35");
        add(pnlTop, BorderLayout.NORTH);

        // --- CENTER TABLE ---
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] cols = { "ID Học viên", "Họ tên", "Điểm số", "Xếp loại", "Nhận xét" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c >= 2;
            }
        };
        tblGrade = new JTable(tableModel);
        setupTable(tblGrade);
        tblGrade.setRowHeight(40);
        pnlCenter.add(new JScrollPane(tblGrade), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- BOTTOM ACTIONS ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBottom.setBackground(Color.WHITE);

        btnSaveTemp = UIHelper.createStandardButton("LƯU TẠM", UIHelper.WARNING_COLOR, "💾");
        btnSendEmail = UIHelper.createStandardButton("GỬI EMAIL", UIHelper.PRIMARY_COLOR, "📧");
        btnExportPdf = UIHelper.createStandardButton("XUẤT PDF", UIHelper.ACCENT_COLOR, "📄");
        btnFinalLock = UIHelper.createStandardButton("CHỐT ĐIỂM", UIHelper.SUCCESS_COLOR, "🔒");

        pnlBottom.add(btnSaveTemp);
        pnlBottom.add(btnSendEmail);
        pnlBottom.add(btnExportPdf);
        pnlBottom.add(btnFinalLock);
        add(pnlBottom, BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> loadStudentGrades());
    }

    @Override
    protected void handleEvents() {
        btnExportPdf.addActionListener(e -> exportCurrentStudentPdf());
        btnSendEmail.addActionListener(e -> sendGradeEmail());
        cmbClass.addActionListener(e -> loadStudentGrades());
    }

    private void sendGradeEmail() {
        int row = tblGrade.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một học viên để gửi điểm!");
            return;
        }

        Student student = currentStudents.get(row);
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            JOptionPane.showMessageDialog(this, "Học viên này chưa có địa chỉ Email!");
            return;
        }

        String score = tableModel.getValueAt(row, 2).toString();
        String grade = tableModel.getValueAt(row, 3).toString();
        String comment = tableModel.getValueAt(row, 4).toString();
        ClassEntity selClass = (ClassEntity) cmbClass.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Gửi điểm cho " + student.getFullName() + " qua email: " + student.getEmail() + "?",
                "Xác nhận gửi email", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                emailService.sendImmediate(
                        student.getEmail(),
                        student.getFullName(),
                        selClass != null ? selClass.getClassName() : "Lớp học",
                        score, grade, comment);
                JOptionPane.showMessageDialog(this, "Đã gửi email thành công tới: " + student.getEmail());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi gửi email: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void exportCurrentStudentPdf() {
        int row = tblGrade.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một học viên để xuất PDF!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("PhieuDiem_" + tableModel.getValueAt(row, 1) + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            ClassEntity selClass = (ClassEntity) cmbClass.getSelectedItem();

            pdfService.exportTranscript(
                    path,
                    tableModel.getValueAt(row, 1).toString(),
                    selClass != null ? selClass.getClassName() : "Lớp học",
                    tableModel.getValueAt(row, 2).toString(),
                    tableModel.getValueAt(row, 3).toString(),
                    tableModel.getValueAt(row, 4).toString());
            JOptionPane.showMessageDialog(this, "Đã xuất PDF thành công tại: " + path);
        }
    }

    private void loadClassData() {
        classController.getAllClasses().forEach(cmbClass::addItem);
    }

    private void loadStudentGrades() {
        ClassEntity selClass = (ClassEntity) cmbClass.getSelectedItem();
        if (selClass == null)
            return;

        tableModel.setRowCount(0);
        currentStudents.clear();

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByClass(selClass.getClass_id());
        for (Enrollment e : enrollments) {
            Student s = e.getStudent();
            currentStudents.add(s);
            // Mặc định điểm và nhận xét (thực tế nên lấy từ bảng Results)
            tableModel.addRow(new Object[] {
                    s.getStudent_id(),
                    s.getFullName(),
                    "0", "Chưa xếp loại", "..."
            });
        }
    }

    @Override
    protected void loadTableData() {
    }
}
