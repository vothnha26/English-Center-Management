package com.trungtamdaotao.view.system;

import javax.swing.*;
import java.awt.*;
import com.trungtamdaotao.model.service.system.account.AuthenticationService;
import com.trungtamdaotao.model.service.system.account.AccountService;
import com.trungtamdaotao.model.service.system.TokenService;
import com.trungtamdaotao.model.dao.system.impl.AccountDAOImpl;
import com.trungtamdaotao.model.dao.system.impl.TokenDAOImpl;

public class ResetPasswordFrame extends JFrame {

    private final AuthenticationService authService;
    private JTextField txtToken;
    private JPasswordField txtNewPassword, txtConfirmPassword;
    private JButton btnReset, btnBack;

    public ResetPasswordFrame() {
        this.authService = new AuthenticationService(new AccountService(new AccountDAOImpl()), new TokenService(new TokenDAOImpl()));
        initUI();
    }

    private void initUI() {
        setTitle("Đặt lại mật khẩu");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Token:"));
        txtToken = new JTextField();
        panel.add(txtToken);

        panel.add(new JLabel("Mật khẩu mới:"));
        txtNewPassword = new JPasswordField();
        panel.add(txtNewPassword);

        panel.add(new JLabel("Xác nhận mật khẩu:"));
        txtConfirmPassword = new JPasswordField();
        panel.add(txtConfirmPassword);

        add(panel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnReset = new JButton("Đặt lại");
        btnBack = new JButton("Quay lại");

        btnReset.addActionListener(e -> doReset());
        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        btnPanel.add(btnReset);
        btnPanel.add(btnBack);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void doReset() {
        String token = txtToken.getText().trim();
        String newPass = new String(txtNewPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

        if (token.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
            return;
        }
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp.");
            return;
        }

        try {
            authService.resetPassword(token, newPass);
            JOptionPane.showMessageDialog(this, "Mật khẩu đã được đặt lại thành công.");
            new LoginFrame().setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}