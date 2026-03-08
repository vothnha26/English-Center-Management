package com.trungtamdaotao.view.system;

import javax.swing.*;
import java.awt.*;
import com.trungtamdaotao.model.service.system.AuthenticationService;
import com.trungtamdaotao.model.service.system.account.AccountService;
import com.trungtamdaotao.model.service.system.TokenService;
import com.trungtamdaotao.model.dao.impl.AccountDAOImpl;
import com.trungtamdaotao.model.dao.impl.TokenDAOImpl;

public class ForgotPasswordFrame extends JFrame {

    private final AuthenticationService authService;
    private JTextField txtEmail;
    private JButton btnSend, btnBack;

    public ForgotPasswordFrame() {
        this.authService = new AuthenticationService(new AccountService(new AccountDAOImpl()), new TokenService(new TokenDAOImpl()));
        initUI();
    }

    private void initUI() {
        setTitle("Quên mật khẩu");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        add(panel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnSend = new JButton("Gửi yêu cầu");
        btnBack = new JButton("Quay lại");

        btnSend.addActionListener(e -> doSend());
        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        btnPanel.add(btnSend);
        btnPanel.add(btnBack);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void doSend() {
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email.");
            return;
        }

        try {
            authService.forgotPassword(email);
            JOptionPane.showMessageDialog(this, "Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn.");
            new ResetPasswordFrame().setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}