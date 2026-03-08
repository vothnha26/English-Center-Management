package com.trungtamdaotao.view;

import javax.swing.*;
import java.awt.*;

public class ActivationFrame extends JFrame {
    private JTextField txtUsername;
    private JTextField txtToken;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnActivate;
    private JButton btnCancel;

    public ActivationFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Kích Hoạt Tài Khoản");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        JLabel lblTitle = new JLabel("KÍCH HOẠT TÀI KHOẢN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 1;
        add(new JLabel("Tài khoản (Email):"), gbc);
        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        add(txtUsername, gbc);

        // Token
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Mã kích hoạt:"), gbc);
        txtToken = new JTextField(20);
        gbc.gridx = 1;
        add(txtToken, gbc);

        // New Password
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Mật khẩu mới:"), gbc);
        txtNewPassword = new JPasswordField(20);
        gbc.gridx = 1;
        add(txtNewPassword, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Xác nhận mật khẩu:"), gbc);
        txtConfirmPassword = new JPasswordField(20);
        gbc.gridx = 1;
        add(txtConfirmPassword, gbc);

        // Nút Kích hoạt
        btnActivate = new JButton("Kích Hoạt");
        btnActivate.setBackground(new Color(0, 123, 255));
        btnActivate.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(btnActivate, gbc);

        // Nút Hủy
        btnCancel = new JButton("Hủy");
        btnCancel.setBorderPainted(false);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setForeground(Color.BLUE);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        add(btnCancel, gbc);
    }

    // Getters để Controller có thể truy cập dữ liệu
    public JTextField getTxtUsername() { return txtUsername; }
    public JTextField getTxtToken() { return txtToken; }
    public JPasswordField getTxtNewPassword() { return txtNewPassword; }
    public JPasswordField getTxtConfirmPassword() { return txtConfirmPassword; }
    public JButton getBtnActivate() { return btnActivate; }
    public JButton getBtnCancel() { return btnCancel; }
}