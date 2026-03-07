package com.trungtamdaotao.controller.system;

import com.trungtamdaotao.model.service.system.account.AuthService;
import java.util.function.Consumer;

public class ResetPasswordController {
    private final AuthService authService;

    public ResetPasswordController(AuthService authService) {
        this.authService = authService;
    }

    public void executeReset(String email, String otp, String newPass,
                             Runnable onSuccess, Consumer<String> onFailure) {

        if (otp.isEmpty() || newPass.isEmpty()) {
            onFailure.accept("Mã OTP và mật khẩu mới không được trống!");
            return;
        }

        authService.resetPassword(email, otp, newPass, onSuccess, onFailure);
    }
}