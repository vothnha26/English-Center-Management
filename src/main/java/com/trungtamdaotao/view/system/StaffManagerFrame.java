package com.trungtamdaotao.view.system;

import com.trungtamdaotao.controller.system.StaffController;
import com.trungtamdaotao.model.dao.system.impl.AccountDAOImpl;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.service.system.account.AccountService;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffManagerFrame extends BaseManagerFrame {

    private final StaffController controller;
    private final AccountService accountService;

    private JTable tblStaff;
    private DefaultTableModel tableModel;

    private JTextField txtName, txtPhone, txtEmail, txtSearch;
    private JComboBox<StaffRole> cbRole;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnReload, btnInactiveAccount, btnSendVerify;

    public StaffManagerFrame() {
        super("Quản lý Nhân viên");
        this.controller = new StaffController();
        this.accountService = new AccountService(new AccountDAOImpl());
        loadTableData();
    }

    @Override
    protected void initComponents() {
        // --- Toolbar (NORTH) ---
        JPanel pnlToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlToolbar.setBackground(UIHelper.PRIMARY_COLOR);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setForeground(Color.WHITE);
        lblSearch.setFont(UIHelper.BOLD_FONT);
        pnlToolbar.add(lblSearch);

        txtSearch = new JTextField(25);
        pnlToolbar.add(txtSearch);

        btnSearch = UIHelper.createStandardButton("Tìm", Color.WHITE, "🔍");
        btnSearch.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnSearch);

        btnReload = UIHelper.createStandardButton("Tải lại", Color.WHITE, "⟳");
        btnReload.setForeground(UIHelper.PRIMARY_COLOR);
        pnlToolbar.add(btnReload);

        add(pnlToolbar, BorderLayout.NORTH);

        // --- Form (WEST) ---
        JPanel pnlForm = UIHelper.createFormPanel("Thông tin nhân viên");
        pnlForm.setPreferredSize(new Dimension(400, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFormField(pnlForm, "Họ tên (*):", txtName = new JTextField(), gbc, row++);
        
        gbc.gridx = 0; gbc.gridy = row;
        pnlForm.add(createFieldLabel("Vai trò (*):"), gbc);
        gbc.gridx = 1;
        cbRole = new JComboBox<>(StaffRole.values());
        pnlForm.add(cbRole, gbc);
        row++;

        addFormField(pnlForm, "Điện thoại (*):", txtPhone = new JTextField(), gbc, row++);
        addFormField(pnlForm, "Email:", txtEmail = new JTextField(), gbc, row++);

        // Buttons Panel
        JPanel pnlButtons = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlButtons.setOpaque(false);
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnAdd = UIHelper.createStandardButton("Thêm", UIHelper.SUCCESS_COLOR, "✚");
        btnUpdate = UIHelper.createStandardButton("Sửa", UIHelper.WARNING_COLOR, "✎");
        btnDelete = UIHelper.createStandardButton("Xóa", UIHelper.DANGER_COLOR, "✘");
        btnClear = UIHelper.createStandardButton("Mới", UIHelper.PRIMARY_COLOR, "⟲");
        btnInactiveAccount = UIHelper.createStandardButton("Khóa TK", Color.GRAY, "🚫");
        btnSendVerify = UIHelper.createStandardButton("Gửi Email", Color.BLUE, "📧");

        pnlButtons.add(btnAdd); pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete); pnlButtons.add(btnClear);
        pnlButtons.add(btnInactiveAccount); pnlButtons.add(btnSendVerify);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        pnlForm.add(pnlButtons, gbc);

        add(pnlForm, BorderLayout.WEST);

        // --- Table (CENTER) ---
        String[] columns = {"ID", "Họ tên", "Vai trò", "Điện thoại", "Email", "Tài khoản"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblStaff = new JTable(tableModel);
        setupTable(tblStaff);
        add(new JScrollPane(tblStaff), BorderLayout.CENTER);
    }

    private void addFormField(JPanel p, String label, JTextField tf, GridBagConstraints gbc, int r) {
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 1;
        p.add(createFieldLabel(label), gbc);
        gbc.gridx = 1;
        p.add(tf, gbc);
    }

    @Override
    protected void handleEvents() {
        btnSearch.addActionListener(e -> doSearch());
        btnReload.addActionListener(e -> loadTableData());
        btnAdd.addActionListener(e -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnClear.addActionListener(e -> clearForm());
        btnInactiveAccount.addActionListener(e -> doInactiveAccount());
        btnSendVerify.addActionListener(e -> doSendVerify());

        tblStaff.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });
    }

    @Override
    protected void loadTableData() {
        if (controller != null) renderTable(controller.getAllStaff());
    }

    private void renderTable(List<Staff> list) {
        tableModel.setRowCount(0);
        for (Staff s : list) {
            String accStatus = "No Account";
            if (s.getEmail() != null && !s.getEmail().isBlank()) {
                var acc = accountService.findByUsername(s.getEmail());
                if (acc != null) accStatus = acc.isIs_active() ? "Active" : "Inactive";
            }
            tableModel.addRow(new Object[]{ s.getStaff_id(), s.getFullName(), s.getRole(), s.getPhone(), s.getEmail(), accStatus });
        }
    }

    private void populateForm() {
        int row = tblStaff.getSelectedRow();
        if (row < 0) return;
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        cbRole.setSelectedItem(tableModel.getValueAt(row, 2));
        txtPhone.setText(tableModel.getValueAt(row, 3).toString());
        txtEmail.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
    }

    private void doSearch() {
        renderTable(controller.searchStaff(txtSearch.getText()));
    }

    private void doAdd() {
        try {
            controller.addStaff(txtName.getText(), (StaffRole) cbRole.getSelectedItem(), txtPhone.getText(), txtEmail.getText());
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
            clearForm(); loadTableData();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    private void doUpdate() {
        int row = tblStaff.getSelectedRow();
        if (row < 0) return;
        try {
            Long id = (Long) tableModel.getValueAt(row, 0);
            Staff s = controller.getStaffById(id);
            s.setFullName(txtName.getText());
            s.setRole((StaffRole) cbRole.getSelectedItem());
            s.setPhone(txtPhone.getText());
            s.setEmail(txtEmail.getText());
            controller.updateStaff(s);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadTableData();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    private void doDelete() {
        int row = tblStaff.getSelectedRow();
        if (row < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Vô hiệu hóa nhân viên này?") == JOptionPane.YES_OPTION) {
            try {
                controller.deleteStaff((Long) tableModel.getValueAt(row, 0));
                clearForm(); loadTableData();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }

    private void clearForm() {
        txtName.setText(""); txtPhone.setText(""); txtEmail.setText(""); cbRole.setSelectedIndex(0);
        tblStaff.clearSelection();
    }

    private void doInactiveAccount() {
        if (txtEmail.getText().isBlank()) return;
        if (JOptionPane.showConfirmDialog(this, "Khóa tài khoản này?") == JOptionPane.YES_OPTION) {
            try {
                var acc = accountService.findByUsername(txtEmail.getText());
                if (acc != null) { acc.setIs_active(false); accountService.updateAccount(acc); loadTableData(); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }

    private void doSendVerify() {
        if (txtEmail.getText().isBlank()) return;
        try { controller.resendVerification(txtEmail.getText()); JOptionPane.showMessageDialog(this, "Đã gửi email xác thực."); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffManagerFrame().setVisible(true));
    }
}
