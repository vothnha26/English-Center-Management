package com.trungtamdaotao.controller.system;

import com.trungtamdaotao.model.dao.impl.ActivationTokenDAOImpl;
import com.trungtamdaotao.model.dao.impl.UserAccountDAOImpl;
import com.trungtamdaotao.model.service.common.RealMailServiceImpl;
import com.trungtamdaotao.model.service.system.account.AccountProvisionService;
import com.trungtamdaotao.view.ActivationFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActivationController {
    private final ActivationFrame activationFrame;
    private final AccountProvisionService accountProvisionService;

    public ActivationController() {
        this.activationFrame = new ActivationFrame();
        this.accountProvisionService = new AccountProvisionService(
            new UserAccountDAOImpl(),
            new ActivationTokenDAOImpl(),
            new RealMailServiceImpl()
        );

        initEventHandlers();
    }

    private void initEventHandlers() {
        activationFrame.getBtnActivate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleActivation();
            }
        });

        activationFrame.getBtnCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activationFrame.dispose();
            }
        });
    }

    private void handleActivation() {
        String username = activationFrame.getTxtUsername().getText().trim();
        String token = activationFrame.getTxtToken().getText().trim();
        String newPassword = new String(activationFrame.getTxtNewPassword().getPassword());
        String confirmPassword = new String(activationFrame.getTxtConfirmPassword().getPassword());

        // Validation
        if (username.isEmpty() || token.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(activationFrame, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(activationFrame, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(activationFrame, "Mật khẩu phải có ít nhất 6 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Activate account
        accountProvisionService.activateAccount(username, token, newPassword,
            () -> {
                JOptionPane.showMessageDialog(activationFrame, "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                activationFrame.dispose();
                // Có thể mở LoginFrame ở đây
            },
            errorMessage -> {
                JOptionPane.showMessageDialog(activationFrame, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        );
    }

    public void showActivationFrame() {
        activationFrame.setVisible(true);
    }
}