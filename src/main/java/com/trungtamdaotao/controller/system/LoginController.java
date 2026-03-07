package com.trungtamdaotao.controller.system;

import com.trungtamdaotao.model.service.system.account.AuthService;
import com.trungtamdaotao.model.entity.system.UserAccount;
import java.util.function.Consumer;

public class LoginController {
    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
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