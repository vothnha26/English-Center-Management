package com.trungtamdaotao.view.system;

import com.trungtamdaotao.controller.system.StaffController;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Quản lý nhân sự - Tích hợp hệ thống.
 */
public class StaffManagerPanel extends BaseManagerPanel {
    private final StaffController staffController;
    private JTable tblStaff;
    private DefaultTableModel tableModel;
    private JTextField txtFullName, txtPhone, txtEmail, txtUsername, txtSearch;
    private JComboBox<StaffRole> cmbRole;
    private JComboBox<Status> cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;

    public StaffManagerPanel() {
        super();
        this.staffController = new StaffController();
        loadTableData();
    }

    @Override
    protected void initComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(750);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);

        // --- MASTER SIDE ---
        JPanel pnlMaster = new JPanel(new BorderLayout(0, 10));
        pnlMaster.setOpaque(false);
        
        JPanel pnlSearch = new JPanel(new MigLayout("insets 0", "[grow]5[]"));
        pnlSearch.setOpaque(false);
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm nhân viên...");
        btnSearch = UIHelper.createStandardButton("Tìm", UIHelper.PRIMARY_COLOR, "\uD83D\uDD0D");
        pnlSearch.add(txtSearch, "grow, height 32");
        pnlSearch.add(btnSearch, "height 32");
        pnlMaster.add(pnlSearch, BorderLayout.NORTH);

        String[] columns = {"ID", "Họ tên", "Số điện thoại", "Email", "Chức vụ", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblStaff = new JTable(tableModel);
        setupTable(tblStaff);
        pnlMaster.add(new JScrollPane(tblStaff), BorderLayout.CENTER);
        splitPane.setLeftComponent(pnlMaster);

        // --- DETAIL SIDE ---
        JPanel pnlDetail = new JPanel(new BorderLayout());
        pnlDetail.setBackground(Color.WHITE);
        pnlDetail.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JLabel lblTitle = new JLabel("HỒ SƠ NHÂN VIÊN");
        lblTitle.setFont(UIHelper.TITLE_FONT);
        lblTitle.setForeground(UIHelper.PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(20, 0, 15, 0));
        pnlDetail.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel(new MigLayout("wrap 2, inset 15, fillx", "[][grow, fill]", "[]10[]10[]10[]10[]10[]10[]20[]"));
        pnlForm.setOpaque(false);

        pnlForm.add(createFieldLabel("Họ tên:")); pnlForm.add(txtFullName = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Điện thoại:")); pnlForm.add(txtPhone = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Email:")); pnlForm.add(txtEmail = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Tên đăng nhập:")); pnlForm.add(txtUsername = new JTextField(), "height 32");
        pnlForm.add(createFieldLabel("Chức vụ:")); pnlForm.add(cmbRole = new JComboBox<>(StaffRole.values()), "height 32");
        pnlForm.add(createFieldLabel("Trạng thái:")); pnlForm.add(cmbStatus = new JComboBox<>(Status.values()), "height 32");

        JPanel pnlActions = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlActions.setOpaque(false);
        btnAdd = UIHelper.createStandardButton("Thêm nhân viên", UIHelper.SUCCESS_COLOR, "\u2795");
        btnUpdate = UIHelper.createStandardButton("Cập nhật", UIHelper.WARNING_COLOR, "\u270E");
        btnDelete = UIHelper.createStandardButton("Vô hiệu hóa", UIHelper.DANGER_COLOR, "\u2718");
        btnClear = UIHelper.createStandardButton("Làm mới", UIHelper.ACCENT_COLOR, "\u21B6");
        pnlActions.add(btnAdd); pnlActions.add(btnUpdate); pnlActions.add(btnDelete); pnlActions.add(btnClear);
        pnlForm.add(pnlActions, "span 2, growx");

        pnlDetail.add(pnlForm, BorderLayout.CENTER);
        splitPane.setRightComponent(pnlDetail);
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    protected void handleEvents() {
        btnSearch.addActionListener(e -> loadTableData());
        btnClear.addActionListener(e -> { txtFullName.setText(""); txtUsername.setText(""); tblStaff.clearSelection(); });
        tblStaff.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblStaff.getSelectedRow() >= 0) {
                int row = tblStaff.getSelectedRow();
                txtFullName.setText(tableModel.getValueAt(row, 1).toString());
                txtPhone.setText(tableModel.getValueAt(row, 2).toString());
                txtEmail.setText(tableModel.getValueAt(row, 3).toString());
                cmbRole.setSelectedItem(tableModel.getValueAt(row, 4));
                cmbStatus.setSelectedItem(tableModel.getValueAt(row, 5));
            }
        });
    }

    @Override
    protected void loadTableData() {
        if (staffController == null) return;
        List<Staff> list = staffController.getAllStaff();
        tableModel.setRowCount(0);
        for (Staff s : list) {
            tableModel.addRow(new Object[]{ s.getStaff_id(), s.getFullName(), s.getPhone(), s.getEmail(), s.getRole(), s.getStatus() });
        }
    }
}
