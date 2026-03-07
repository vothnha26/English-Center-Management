package com.trungtamdaotao.view;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnForgot;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ Thống Quản Lý Trung Tâm - Đăng Nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Hiển thị giữa màn hình
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 1;
        add(new JLabel("Tài khoản (Email):"), gbc);
        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Mật khẩu:"), gbc);
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // Nút Đăng nhập
        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBackground(new Color(0, 123, 255));
        btnLogin.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(btnLogin, gbc);

        // Nút Quên mật khẩu
        btnForgot = new JButton("Quên mật khẩu?");
        btnForgot.setBorderPainted(false);
        btnForgot.setContentAreaFilled(false);
        btnForgot.setForeground(Color.BLUE);
        btnForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        add(btnForgot, gbc);
    }

    // Getters để Controller có thể truy cập dữ liệu (Encapsulation)
    public JTextField getTxtUsername() { return txtUsername; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnLogin() { return btnLogin; }
    public JButton getBtnForgot() { return btnForgot; }
}