package com.trungtamdaotao.model.service.system.account;

import com.trungtamdaotao.model.dao.system.IUserAccountDAO;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.service.common.IMailService;
import com.trungtamdaotao.util.GlobalSession;
import com.trungtamdaotao.util.PasswordUtil;
import com.trungtamdaotao.util.TokenGenerator;

import java.util.function.Consumer;

public class AuthService {
    private final IUserAccountDAO accountDAO;
    private final IMailService mailService;

    public AuthService(IUserAccountDAO accountDAO, IMailService mailService) {
        this.accountDAO = accountDAO;
        this.mailService = mailService;
    }

    /**
     * Đăng nhập: Kiểm tra Username, Active và Password
     */
    public void login(String username, String password,
                      Consumer<UserAccount> onSuccess, Consumer<String> onFailure) {

        UserAccount account = accountDAO.findByUsername(username);

        if (account == null) {
            onFailure.accept("Tài khoản không tồn tại!");
            return;
        }

        if (!account.isIs_active()) {
            onFailure.accept("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra Email!");
            return;
        }

        if (PasswordUtil.checkPassword(password, account.getPassword_hash())) {
            GlobalSession.login(account); // Lưu vào phiên làm việc
            onSuccess.accept(account);
        } else {
            onFailure.accept("Mật khẩu không chính xác!");
        }
    }

    /**
     * Quên mật khẩu: Gửi mã OTP mới qua Email
     */
    public void forgotPassword(String email, Runnable onSuccess, Consumer<String> onFailure) {
        UserAccount account = accountDAO.findByUsername(email);

        if (account == null) {
            onFailure.accept("Email này không tồn tại trên hệ thống!");
            return;
        }

        // Tạo mã OTP reset
        String resetToken = TokenGenerator.generateOTP();

        // Lưu mã OTP vào password_hash (như mật khẩu tạm thời) và khóa Active
        account.setPassword_hash(PasswordUtil.hashPassword(resetToken));
        account.setIs_active(false);

        try {
            accountDAO.update(account);
            mailService.sendActivationEmail(email, resetToken);
            onSuccess.run();
        } catch (Exception e) {
            onFailure.accept("Lỗi hệ thống khi gửi mã reset!");
        }
    }

    /**
     * Bước 2 của Quên mật khẩu: Xác nhận OTP và đặt mật khẩu mới
     */
    public void resetPassword(String email, String otpToken, String newPassword,
                              Runnable onSuccess, Consumer<String> onFailure) {

        UserAccount account = accountDAO.findByUsername(email);

        if (account == null) {
            onFailure.accept("Tài khoản không tồn tại!");
            return;
        }

        // 1. Kiểm tra mã OTP (đang nằm trong password_hash)
        if (PasswordUtil.checkPassword(otpToken, account.getPassword_hash())) {

            // 2. Nếu khớp, Hash mật khẩu mới thật sự
            account.setPassword_hash(PasswordUtil.hashPassword(newPassword));
            account.setIs_active(true); // Kích hoạt lại tài khoản

            try {
                accountDAO.update(account);
                onSuccess.run();
            } catch (Exception e) {
                onFailure.accept("Lỗi hệ thống khi cập nhật mật khẩu!");
            }
        } else {
            onFailure.accept("Mã OTP không chính xác hoặc đã hết hạn!");
        }
    }

    /**
     * Đăng xuất
     */
    public void logout() {
        GlobalSession.logout();
    }
}