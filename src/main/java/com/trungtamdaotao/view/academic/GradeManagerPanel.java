package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.service.common.EmailFeedbackService;
import com.trungtamdaotao.model.service.common.PdfExportService;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Quản lý điểm số tích hợp Gửi Email và Xuất PDF phiếu điểm.
 */
public class GradeManagerPanel extends BaseManagerPanel {
    private final ClassController classController;
    private final EmailFeedbackService emailService;
    private final PdfExportService pdfService;
    
    private JComboBox<ClassEntity> cmbClass;
    private JTable tblGrade;
    private DefaultTableModel tableModel;
    private JButton btnSaveTemp, btnFinalLock, btnSendEmail, btnExportPdf;

    public GradeManagerPanel() {
        super();
        this.classController = new ClassController();
        this.emailService = new EmailFeedbackService();
        this.pdfService = new PdfExportService();
        loadClassData();
    }

    @Override
    protected void initComponents() {
        // --- TOP SELECTION ---
        JPanel pnlTop = new JPanel(new MigLayout("insets 20, fillx", "[][grow]20[]"));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(createFieldLabel("Chọn lớp học:"));
        cmbClass = new JComboBox<>();
        pnlTop.add(cmbClass, "grow, height 35");
        pnlTop.add(UIHelper.createStandardButton("Tải danh sách", UIHelper.ACCENT_COLOR, ""), "height 35");
        add(pnlTop, BorderLayout.NORTH);

        // --- CENTER TABLE ---
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] cols = {"ID Học viên", "Họ tên", "Điểm số", "Xếp loại", "Nhận xét"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c >= 2; }
        };
        tblGrade = new JTable(tableModel);
        setupTable(tblGrade);
        tblGrade.setRowHeight(40);
        pnlCenter.add(new JScrollPane(tblGrade), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // --- BOTTOM ACTIONS ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBottom.setBackground(Color.WHITE);

        btnSaveTemp = UIHelper.createStandardButton("LƯU TẠM", UIHelper.WARNING_COLOR, "");
        btnSendEmail = UIHelper.createStandardButton("GỬI EMAIL", UIHelper.PRIMARY_COLOR, "");
        btnExportPdf = UIHelper.createStandardButton("XUẤT PDF", UIHelper.ACCENT_COLOR, "");
        btnFinalLock = UIHelper.createStandardButton("CHỐT ĐIỂM", UIHelper.SUCCESS_COLOR, "");

        pnlBottom.add(btnSaveTemp);
        pnlBottom.add(btnSendEmail);
        pnlBottom.add(btnExportPdf);
        pnlBottom.add(btnFinalLock);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    @Override
    protected void handleEvents() {
        btnExportPdf.addActionListener(e -> exportCurrentStudentPdf());
        btnSendEmail.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tính năng Gửi Email đã sẵn sàng."));
        cmbClass.addActionListener(e -> loadStudentGrades());
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
                tableModel.getValueAt(row, 4).toString()
            );
            JOptionPane.showMessageDialog(this, "Đã xuất PDF thành công tại: " + path);
        }
    }

    private void loadClassData() {
        classController.getAllClasses().forEach(cmbClass::addItem);
    }

    private void loadStudentGrades() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"HV001", "Nguyễn Văn A", "8.5", "Giỏi", "Tiếp thu bài nhanh."});
        tableModel.addRow(new Object[]{"HV002", "Trần Thị B", "9.5", "Xuất sắc", "Hoàn thành tốt các bài tập."});
    }

    @Override protected void loadTableData() {}
}
