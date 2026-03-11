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
        btnAdd.addActionListener(e -> {
            try {
                String name = txtFullName.getText();
                String phone = txtPhone.getText();
                String email = txtEmail.getText();
                String specialty = txtSpecialty.getText();
                
                teacherController.addTeacher(name, phone, email, specialty, java.time.LocalDate.now());
                JOptionPane.showMessageDialog(this, "Them giao vien thanh cong!");
                loadTableData();
                btnClear.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = tblTeacher.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui long chon giao vien can sua!");
                return;
            }
            try {
                Long id = (Long) tableModel.getValueAt(row, 0);
                Teacher t = teacherController.getTeacherById(id);
                t.setFullName(txtFullName.getText());
                t.setPhone(txtPhone.getText());
                t.setEmail(txtEmail.getText());
                t.setSpecialty(txtSpecialty.getText());
                t.setStatus((Status) cmbStatus.getSelectedItem());
                
                teacherController.updateTeacher(t);
                JOptionPane.showMessageDialog(this, "Cap nhat thanh cong!");
                loadTableData();
                btnClear.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = tblTeacher.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui long chon giao vien can xoa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Ban co chac muon xoa (ngung hoat dong) giao vien nay?", "Xac nhan", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Long id = (Long) tableModel.getValueAt(row, 0);
                teacherController.deleteTeacher(id);
                loadTableData();
                btnClear.doClick();
            }
        });

        btnClear.addActionListener(e -> { 
            txtFullName.setText(""); 
            txtPhone.setText(""); 
            txtEmail.setText(""); 
            txtSpecialty.setText(""); 
            cmbStatus.setSelectedIndex(0);
            tblTeacher.clearSelection(); 
        });

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
        String keyword = txtSearch.getText();
        List<Teacher> list;
        if (keyword == null || keyword.isBlank()) {
            list = teacherController.getAllTeachers();
        } else {
            list = teacherController.searchTeachers(keyword);
        }
        
        tableModel.setRowCount(0);
        list.forEach(t -> tableModel.addRow(new Object[]{ 
            t.getTeacher_id(), t.getFullName(), t.getPhone(), 
            t.getEmail(), t.getSpecialty(), t.getStatus() 
        }));
    }
}
