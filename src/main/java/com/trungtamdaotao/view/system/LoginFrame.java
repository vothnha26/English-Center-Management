package com.trungtamdaotao.view.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.service.system.account.AuthenticationService;
import com.trungtamdaotao.model.service.system.account.AccountService;
import com.trungtamdaotao.model.service.system.TokenService;
import com.trungtamdaotao.model.dao.impl.AccountDAOImpl;
import com.trungtamdaotao.model.dao.impl.TokenDAOImpl;
import com.trungtamdaotao.model.entity.enums.TokenType;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private AuthenticationService authService;

    public LoginFrame() {
        this.authService = new AuthenticationService(new AccountService(new AccountDAOImpl()), new TokenService(new TokenDAOImpl()));
        initUI();
    }

    private void initUI() {
        setTitle("Đăng nhập");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField();
        panel.add(txtUsername);

        panel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        add(panel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnLogin = new JButton("Đăng nhập");
        JButton btnForgot = new JButton("Quên mật khẩu");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
        btnForgot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ForgotPasswordFrame().setVisible(true);
                dispose();
            }
        });
        btnPanel.add(btnLogin);
        btnPanel.add(btnForgot);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void doLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            var result = authService.login(username, password);
            UserAccount account = result.getAccount();

            if (result.isNeedChangePassword()) {
                // Show change password dialog, mandatory
                ChangePasswordDialog dialog = new ChangePasswordDialog(this, account);
                dialog.setVisible(true);

                if (!dialog.isChanged()) {
                    // Revert active = false
                    account.setIs_active(false);
                    var accountService = new AccountService(new AccountDAOImpl());
                    accountService.updateAccount(account);
                    JOptionPane.showMessageDialog(this, "Phải đặt mật khẩu mới để hoàn tất xác thực.");
                    return;
                } else {
                    // After change, mark token as used
                    var tokenService = new TokenService(new TokenDAOImpl());
                    var token = tokenService.findValidTokenByUserAndType(account, TokenType.EMAIL_VERIFICATION);
                    if (token != null) {
                        tokenService.markAsUsed(token.getToken_value());
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Vai trò: " + account.getRole());

            // Mở main frame dựa trên role
            if (account.getRole().toString().equals("ADMIN") || account.getRole().toString().equals("STAFF")) {
                new StaffManagerFrame().setVisible(true);
            }
            // Có thể thêm cho teacher, student

            dispose(); // Đóng login frame
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đăng nhập: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}