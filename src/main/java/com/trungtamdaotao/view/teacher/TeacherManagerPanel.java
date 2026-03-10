package com.trungtamdaotao.view.teacher;

import com.trungtamdaotao.controller.teacher.TeacherController;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeacherManagerPanel extends BaseManagerPanel {
    private final TeacherController teacherController;
    private JTable tblTeacher;
    private DefaultTableModel tableModel;
    private JTextField txtFullName, txtPhone, txtEmail, txtSpecialty, txtSearch;
    private JComboBox<Status> cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReload;

    public TeacherManagerPanel() {
        super();
        this.teacherController = new TeacherController();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(700);
        splitPane.setDividerSize(10);
        splitPane.setOpaque(false);

        // Master
        JPanel pnlMaster = new JPanel(new BorderLayout(0, 15));
        pnlMaster.setOpaque(false);
        JPanel pnlSearch = new JPanel(new MigLayout("insets 0", "[grow]10[]"));
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tim kiem giao vien...");
        btnSearch = UIHelper.createStandardButton("Tim", UIHelper.PRIMARY_COLOR, "");
        pnlSearch.add(txtSearch, "grow, height 35");
        pnlSearch.add(btnSearch, "height 35");
        pnlMaster.add(pnlSearch, BorderLayout.NORTH);

        String[] columns = {"ID", "Ho ten", "SDT", "Email", "Chuyen mon", "Trang thai"};
        tableModel = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblTeacher = new JTable(tableModel);
        setupTable(tblTeacher);
        pnlMaster.add(new JScrollPane(tblTeacher), BorderLayout.CENTER);
        splitPane.setLeftComponent(pnlMaster);

        // Detail
        JPanel pnlDetail = new JPanel(new MigLayout("wrap 2, inset 20, fillx", "[][grow, fill]", "[]20[]15[]15[]15[]15[]15[]25[]"));
        pnlDetail.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("HO SO GIAO VIEN");
        lblTitle.setFont(UIHelper.TITLE_FONT);
        lblTitle.setForeground(UIHelper.PRIMARY_COLOR);
        pnlDetail.add(lblTitle, "span 2, center");

        pnlDetail.add(createFieldLabel("Ho ten:")); pnlDetail.add(txtFullName = new JTextField(), "height 35");
        pnlDetail.add(createFieldLabel("Dien thoai:")); pnlDetail.add(txtPhone = new JTextField(), "height 35");
        pnlDetail.add(createFieldLabel("Email:")); pnlDetail.add(txtEmail = new JTextField(), "height 35");
        pnlDetail.add(createFieldLabel("Chuyen mon:")); pnlDetail.add(txtSpecialty = new JTextField(), "height 35");
        pnlDetail.add(createFieldLabel("Trang thai:")); pnlDetail.add(cmbStatus = new JComboBox<>(Status.values()), "height 35");

        JPanel pnlBtns = new JPanel(new GridLayout(2, 2, 8, 8)); pnlBtns.setOpaque(false);
        btnAdd = UIHelper.createStandardButton("Them", UIHelper.SUCCESS_COLOR, "");
        btnUpdate = UIHelper.createStandardButton("Sua", UIHelper.WARNING_COLOR, "");
        btnDelete = UIHelper.createStandardButton("Xoa", UIHelper.DANGER_COLOR, "");
        btnClear = UIHelper.createStandardButton("Moi", UIHelper.ACCENT_COLOR, "");
        pnlBtns.add(btnAdd); pnlBtns.add(btnUpdate); pnlBtns.add(btnDelete); pnlBtns.add(btnClear);
        pnlDetail.add(pnlBtns, "span 2, growx");

        splitPane.setRightComponent(pnlDetail);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override protected void handleEvents() {
        btnSearch.addActionListener(e -> loadTableData());
        btnClear.addActionListener(e -> { txtFullName.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtSpecialty.setText(""); tblTeacher.clearSelection(); });
        tblTeacher.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblTeacher.getSelectedRow() >= 0) {
                int row = tblTeacher.getSelectedRow();
                txtFullName.setText(tableModel.getValueAt(row, 1).toString());
                txtPhone.setText(tableModel.getValueAt(row, 2).toString());
                txtEmail.setText(tableModel.getValueAt(row, 3).toString());
                txtSpecialty.setText(tableModel.getValueAt(row, 4).toString());
                cmbStatus.setSelectedItem(tableModel.getValueAt(row, 5));
            }
        });
    }

    @Override protected void loadTableData() {
        if (teacherController == null) return;
        List<Teacher> list = teacherController.getAllTeachers();
        tableModel.setRowCount(0);
        for (Teacher t : list) {
            tableModel.addRow(new Object[]{ t.getTeacher_id(), t.getFullName(), t.getPhone(), t.getEmail(), t.getSpecialty(), t.getStatus() });
        }
    }
}
