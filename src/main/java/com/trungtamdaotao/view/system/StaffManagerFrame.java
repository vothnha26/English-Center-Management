package com.trungtamdaotao.view.system;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.trungtamdaotao.controller.system.StaffController;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.service.system.account.AccountService;
import com.trungtamdaotao.model.dao.impl.AccountDAOImpl;

/**
 * Màn hình Quản lý Staff (CRUD).
 * Layout: thanh tìm kiếm trên đầu, bảng danh sách ở giữa, form nhập liệu bên dưới.
 */
public class StaffManagerFrame extends JFrame {

    private final StaffController controller;
    private final AccountService accountService;

    // Bảng danh sách
    private JTable table;
    private DefaultTableModel tableModel;

    // Ô tìm kiếm
    private JTextField txtSearch;

    // Form nhập liệu
    private JTextField txtId, txtName, txtPhone, txtEmail;
    private JComboBox<StaffRole> cbRole;

    // Nút hành động
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnInactiveAccount, btnSendVerify;

    private static final String[] COLUMNS = {"ID", "Họ tên", "Vai trò", "Điện thoại", "Email", "Trạng thái"};

    public StaffManagerFrame() {
        this.controller = new StaffController();
        this.accountService = new AccountService(new AccountDAOImpl());
        initUI();
        loadTable(controller.getAllStaff());
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void initUI() {
        setTitle("Quản lý Staff");
        setSize(960, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildFormPanel(),   BorderLayout.SOUTH);
    }

    // ── Top: thanh tìm kiếm ──────────────────────────────────────────────────
    private JPanel buildTopPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));

        txtSearch = new JTextField(24);
        btnSearch = new JButton("Tìm");
        JButton btnReload = new JButton("Tải lại");

        btnSearch.addActionListener(e -> doSearch());
        txtSearch.addActionListener(e -> doSearch());
        btnReload.addActionListener(e -> loadTable(controller.getAllStaff()));

        p.add(new JLabel("Tên / SĐT:"));
        p.add(txtSearch);
        p.add(btnSearch);
        p.add(btnReload);
        return p;
    }

    // ── Center: bảng JTable ──────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        return new JScrollPane(table);
    }

    // ── South: form nhập liệu + nút ──────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(4, 4));
        wrapper.setBorder(BorderFactory.createTitledBorder("Thông tin Staff"));

        // Lưới nhập liệu
        JPanel grid = new JPanel(new GridLayout(2, 5, 6, 4));
        txtId      = new JTextField(); txtId.setEditable(false);
        txtName    = new JTextField();
        cbRole     = new JComboBox<>(StaffRole.values());
        txtPhone   = new JTextField();
        txtEmail   = new JTextField();

        grid.add(label("ID:")); grid.add(txtId);
        grid.add(label("Họ tên (*):"));   grid.add(txtName);
        grid.add(label("Vai trò (*):")); grid.add(cbRole);
        grid.add(label("Điện thoại (*):")); grid.add(txtPhone);
        grid.add(label("Email:"));         grid.add(txtEmail);

        // Nút hành động
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        btnAdd    = new JButton("➕ Thêm");
        btnUpdate = new JButton("✏ Cập nhật");
        btnDelete = new JButton("🗑 Xóa (Inactive)");
        btnClear  = new JButton("⬜ Xóa form");
        btnInactiveAccount = new JButton("🚫 Inactive Account");
        btnSendVerify = new JButton("📧 Send Verify Email");

        btnAdd.addActionListener(e    -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnClear.addActionListener(e  -> clearForm());
        btnInactiveAccount.addActionListener(e -> doInactiveAccount());
        btnSendVerify.addActionListener(e -> doSendVerify());

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear);
        btnPanel.add(btnInactiveAccount); btnPanel.add(btnSendVerify);

        wrapper.add(grid, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Nạp dữ liệu vào bảng
    private void loadTable(List<Staff> list) {
        tableModel.setRowCount(0);
        for (Staff s : list) {
            String accountStatus = "No Account";
            if (s.getEmail() != null && !s.getEmail().isBlank()) {
                var account = accountService.findByUsername(s.getEmail());
                if (account != null) {
                    accountStatus = account.isIs_active() ? "Active" : "Inactive";
                }
            }
            tableModel.addRow(new Object[]{
                s.getStaff_id(),
                s.getFullName(),
                s.getRole(),
                s.getPhone(),
                s.getEmail(),
                accountStatus
            });
        }
    }

    // Chọn hàng → điền vào form
    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        cbRole.setSelectedItem(tableModel.getValueAt(row, 2));
        txtPhone.setText(tableModel.getValueAt(row, 3).toString());
        Object email = tableModel.getValueAt(row, 4);
        txtEmail.setText(email != null ? email.toString() : "");
    }

    // ── Hành động CRUD ───────────────────────────────────────────────────────

    private void doSearch() {
        loadTable(controller.searchStaff(txtSearch.getText()));
    }

    private void doAdd() {
        try {
            controller.addStaff(txtName.getText(), (StaffRole) cbRole.getSelectedItem(),
                                txtPhone.getText(), txtEmail.getText());
            JOptionPane.showMessageDialog(this, "Thêm staff thành công!");
            clearForm();
            loadTable(controller.getAllStaff());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        if (txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn staff cần cập nhật.");
            return;
        }
        try {
            long id = Long.parseLong(txtId.getText());
            Staff s = controller.getStaffById(id);
            if (s == null) { JOptionPane.showMessageDialog(this, "Không tìm thấy staff."); return; }

            s.setFullName(txtName.getText());
            s.setRole((StaffRole) cbRole.getSelectedItem());
            s.setPhone(txtPhone.getText());
            s.setEmail(txtEmail.getText());
            controller.updateStaff(s);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadTable(controller.getAllStaff());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        if (txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn staff muốn xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Staff sẽ bị đặt Inactive. Tiếp tục?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            controller.deleteStaff(Long.parseLong(txtId.getText()));
            JOptionPane.showMessageDialog(this, "Đã đặt trạng thái Inactive.");
            clearForm();
            loadTable(controller.getAllStaff());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtPhone.setText("");
        txtEmail.setText(""); cbRole.setSelectedIndex(0);
        table.clearSelection();
    }

    private void doInactiveAccount() {
        if (txtId.getText().isBlank() || txtEmail.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn staff có email.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Account sẽ bị inactive. Tiếp tục?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            var account = accountService.findByUsername(txtEmail.getText());
            if (account != null) {
                account.setIs_active(false);
                accountService.updateAccount(account);
                JOptionPane.showMessageDialog(this, "Account đã inactive.");
                loadTable(controller.getAllStaff());
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy account.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doSendVerify() {
        if (txtId.getText().isBlank() || txtEmail.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn staff có email.");
            return;
        }
        try {
            controller.resendVerification(txtEmail.getText());
            JOptionPane.showMessageDialog(this, "Email xác thực đã gửi.");
            loadTable(controller.getAllStaff());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tạo tài khoản thủ công cho staff đã có (kèm xem trước username & role).
     */

    private JLabel label(String text) { return new JLabel(text); }

    // ── Entry point (standalone test) ────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffManagerFrame().setVisible(true));
    }
}