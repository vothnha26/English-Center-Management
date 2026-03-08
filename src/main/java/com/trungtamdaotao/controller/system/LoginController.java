package com.trungtamdaotao.controller.system;

import com.trungtamdaotao.model.service.system.account.AuthService;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.view.ActivationFrame;
import com.trungtamdaotao.view.LoginFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class LoginController {
    private final AuthService authService;
    private final LoginFrame loginFrame;
    private final ActivationController activationController;

    public LoginController(LoginFrame loginFrame, AuthService authService) {
        this.loginFrame = loginFrame;
        this.authService = authService;
        this.activationController = new ActivationController();

        initEventHandlers();
    }

    private void initEventHandlers() {
        loginFrame.getBtnLogin().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        loginFrame.getBtnForgot().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleForgotPassword();
            }
        });

        loginFrame.getBtnActivate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activationController.showActivationFrame();
            }
        });
    }

    private void handleLogin() {
        String user = loginFrame.getTxtUsername().getText().trim();
        String pass = new String(loginFrame.getTxtPassword().getPassword());

        executeLogin(user, pass,
            account -> {
                JOptionPane.showMessageDialog(loginFrame, "Đăng nhập thành công! Chào mừng " + account.getUsername(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                // TODO: Mở main application frame
            },
            error -> {
                JOptionPane.showMessageDialog(loginFrame, error, "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        );
    }

    private void handleForgotPassword() {
        String email = JOptionPane.showInputDialog(loginFrame, "Nhập email của bạn:", "Quên mật khẩu", JOptionPane.QUESTION_MESSAGE);
        if (email != null && !email.trim().isEmpty()) {
            executeForgotPassword(email.trim(),
                () -> {
                    JOptionPane.showMessageDialog(loginFrame, "Đã gửi email đặt lại mật khẩu!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                },
                error -> {
                    JOptionPane.showMessageDialog(loginFrame, error, "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            );
        }
    }

    // Xử lý đăng nhập
    public void executeLogin(String user, String pass,
                             Consumer<UserAccount> onSuccess,
                             Consumer<String> onFailure) {

        if (user.isEmpty() || pass.isEmpty()) {
            onFailure.accept("Tài khoản và mật khẩu không được để trống!");
            return;
        }

        authService.login(user, pass, onSuccess, onFailure);
    }

    // Xử lý yêu cầu quên mật khẩu
    public void executeForgotPassword(String email,
                                      Runnable onSuccess,
                                      Consumer<String> onFailure) {
        if (email.isEmpty()) {
            onFailure.accept("Vui lòng nhập Email!");
            return;
        }

        authService.forgotPassword(email, onSuccess, onFailure);
    }
}