package com.trungtamdaotao.view.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.service.system.account.AuthenticationService;
import com.trungtamdaotao.model.service.system.account.AccountService;
import com.trungtamdaotao.model.service.system.TokenService;
import com.trungtamdaotao.model.dao.system.impl.AccountDAOImpl;
import com.trungtamdaotao.model.dao.system.impl.TokenDAOImpl;
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
        txtUsername.addActionListener(e -> doLogin()); // Enter triggers login
        panel.add(txtUsername);

        panel.add(new JLabel("Mật khẩu:"));
        txtPassword = new JPasswordField();
        txtPassword.addActionListener(e -> doLogin()); // Enter triggers login
        panel.add(txtPassword);

        add(panel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnLogin = new JButton("Đăng nhập");
        JButton btnForgot = new JButton("Quên mật khẩu");
        JButton btnRegister = new JButton("Đăng ký");
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
        btnRegister.addActionListener(e -> new RegistrationFrame().setVisible(true));
        btnPanel.add(btnLogin);
        btnPanel.add(btnForgot);
        btnPanel.add(btnRegister);
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
                JOptionPane.showMessageDialog(this, "Đây là lần đăng nhập đầu tiên hoặc bạn đang dùng mật khẩu tạm. Vui lòng đổi mật khẩu mới để kích hoạt tài khoản.");
                ChangePasswordDialog dialog = new ChangePasswordDialog(this, account);
                dialog.setVisible(true);

                if (dialog.isChanged()) {
                    // Sau khi đổi mật khẩu thành công, kích hoạt tài khoản
                    account.setIs_active(true);
                    new AccountService(new AccountDAOImpl()).updateAccount(account);
                    
                    // Thu hồi toàn bộ token xác thực cũ
                    new TokenService(new TokenDAOImpl()).revokeOldTokens(account, TokenType.EMAIL_VERIFICATION);
                    
                    JOptionPane.showMessageDialog(this, "Tài khoản đã được kích hoạt thành công!");
                } else {
                    // Nếu người dùng hủy đổi mật khẩu, không cho vào hệ thống
                    JOptionPane.showMessageDialog(this, "Bạn phải đổi mật khẩu để hoàn tất kích hoạt tài khoản.");
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");

            // Lưu phiên làm việc
            com.trungtamdaotao.util.security.UserSession.login(account);

            // Mở MainMenuFrame với vai trò của tài khoản đăng nhập
            new com.trungtamdaotao.view.MainMenuFrame(account.getRole()).setVisible(true);

            dispose(); // Đóng login frame
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đăng nhập: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}