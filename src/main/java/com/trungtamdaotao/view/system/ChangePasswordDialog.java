package com.trungtamdaotao.view.system;

import javax.swing.*;
import java.awt.*;
import com.trungtamdaotao.model.service.system.account.AccountService;
import com.trungtamdaotao.model.dao.impl.AccountDAOImpl;
import com.trungtamdaotao.model.entity.system.UserAccount;

public class ChangePasswordDialog extends JDialog {

    private final AccountService accountService;
    private final UserAccount account;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnChange, btnCancel;
    private boolean changed = false;

    public ChangePasswordDialog(Frame parent, UserAccount account) {
        super(parent, "Đặt mật khẩu mới", true);
        this.account = account;
        this.accountService = new AccountService(new AccountDAOImpl());
        initUI();
    }

    private void initUI() {
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Mật khẩu mới:"));
        txtNewPassword = new JPasswordField();
        panel.add(txtNewPassword);

        panel.add(new JLabel("Xác nhận mật khẩu:"));
        txtConfirmPassword = new JPasswordField();
        panel.add(txtConfirmPassword);

        add(panel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnChange = new JButton("Đặt mật khẩu");
        btnCancel = new JButton("Hủy");

        btnChange.addActionListener(e -> doChangePassword());
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnChange);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public boolean isChanged() {
        return changed;
    }

    private void doChangePassword() {
        String newPass = new String(txtNewPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

        if (newPass.isBlank()) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống.");
            return;
        }
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp.");
            return;
        }

        try {
            accountService.changePassword(account, newPass);
            changed = true;
            JOptionPane.showMessageDialog(this, "Đặt mật khẩu thành công!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}