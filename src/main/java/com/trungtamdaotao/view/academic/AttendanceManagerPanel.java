package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.controller.academic.AttendanceController;
import com.trungtamdaotao.controller.academic.ClassController;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
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
 * Panel Điểm danh tích hợp.
 */
public class AttendanceManagerPanel extends BaseManagerPanel {
    private final AttendanceController attendanceController;
    private final ClassController classController;
    
    private JComboBox<ClassEntity> cmbClass;
    private JTextField txtAttendDate;
    private JTable tblChecklist;
    private DefaultTableModel tableModel;
    private JButton btnMarkAll, btnSave, btnStats, btnReload;

    public AttendanceManagerPanel() {
        super();
        this.attendanceController = new AttendanceController();
        this.classController = new ClassController();
        loadClassData();
    }

    @Override
    protected void initComponents() {
        JPanel pnlSelection = new JPanel(new MigLayout("insets 20, fillx", "[][grow]20[][grow]20[]"));
        pnlSelection.setBackground(Color.WHITE);
        pnlSelection.add(createFieldLabel("Chọn lớp học:"));
        cmbClass = new JComboBox<>();
        pnlSelection.add(cmbClass, "grow, height 35");
        pnlSelection.add(createFieldLabel("Ngày:"));
        txtAttendDate = new JTextField(LocalDate.now().toString());
        pnlSelection.add(txtAttendDate, "grow, height 35");
        btnReload = UIHelper.createStandardButton("Tải danh sách", UIHelper.ACCENT_COLOR, "");
        pnlSelection.add(btnReload, "height 35");
        add(pnlSelection, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(15, 20, 15, 20));

        btnMarkAll = UIHelper.createStandardButton("TAT CA CO MAT", UIHelper.PRIMARY_COLOR, "");
        pnlCenter.add(btnMarkAll, BorderLayout.NORTH);

        String[] columns = {"Có mặt", "ID", "Học viên", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int r, int c) { return c == 0 || c == 4; }
        };
        tblChecklist = new JTable(tableModel);
        setupTable(tblChecklist);
        tblChecklist.setRowHeight(45);
        pnlCenter.add(new JScrollPane(tblChecklist), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBottom.setBackground(Color.WHITE);
        btnStats = UIHelper.createStandardButton("Thống kê", UIHelper.ACCENT_COLOR, "");
        btnSave = UIHelper.createStandardButton("LUU DIEM DANH", UIHelper.SUCCESS_COLOR, "");
        pnlBottom.add(btnStats); pnlBottom.add(btnSave);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadClassData() {
        List<ClassEntity> classes = classController.getAllClasses();
        cmbClass.removeAllItems();
        for (ClassEntity c : classes) cmbClass.addItem(c);
    }

    @Override protected void handleEvents() {
        btnReload.addActionListener(e -> loadAttendanceChecklist());
        btnMarkAll.addActionListener(e -> { for(int i=0; i<tableModel.getRowCount(); i++) tableModel.setValueAt(true, i, 0); });
        btnSave.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đã lưu điểm danh!"));
    }

    private void loadAttendanceChecklist() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{true, "HV001", "Nguyễn Văn A", "Có mặt", ""});
        tableModel.addRow(new Object[]{true, "HV002", "Trần Thị B", "Có mặt", ""});
    }

    @Override protected void loadTableData() {}
}
