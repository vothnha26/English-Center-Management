package com.trungtamdaotao.model.service.system.account;

import com.trungtamdaotao.model.dao.system.impl.AccountDAOImpl;
import com.trungtamdaotao.model.dao.system.impl.TokenDAOImpl;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.TokenType;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.entity.system.Token;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.service.common.EmailService;
import com.trungtamdaotao.model.service.system.TokenService;
import com.trungtamdaotao.util.EmailConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.function.Consumer;

public class RegistrationService {

    private final AccountService accountService;
    private final TokenService tokenService;
    private final EmailService emailService;

    public RegistrationService(AccountService accountService, TokenService tokenService, EmailService emailService) {
        this.accountService = accountService;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    /** Constructor mặc định sử dụng thông tin từ .env file và gửi email thực */
    public RegistrationService() {
        this(new AccountService(new AccountDAOImpl()),
             new TokenService(new TokenDAOImpl()),
             new EmailService(EmailConfig.getSmtpHost(), EmailConfig.getSmtpPort(),
                             EmailConfig.getMailUsername(), EmailConfig.getMailPassword(), false));
    }

    public void registerUser(String username, String email, AccountRole role,
                             Consumer<UserAccount> binder) throws Exception {
        // 1. Kiểm tra tài khoản đã tồn tại chưa
        UserAccount account = accountService.findByUsername(username);

        if (account == null) {
            // Phải khởi tạo Object trước khi dùng binder!
            account = new UserAccount();
            account.setUsername(username);
            account.setRole(role);
            account.setIs_active(false); // Mặc định chưa kích hoạt

            // Dùng Binder để gán Teacher/Student/Staff tùy trường hợp
            binder.accept(account);

            // Đặt mật khẩu tạm thời "temp" ban đầu (Hoặc bỏ qua bước này, gán thẳng Token luôn)
            String tempPasswordHash = hashPassword("temp");
            account.setPassword_hash(tempPasswordHash);

            // Lưu Account lần 1 để lấy ID (Cần ID để tạo Token)
            account = accountService.createAccount(account);

            // 2. Tạo Token (24 giờ = 1440 phút)
            Token token = tokenService.generateToken(account, TokenType.EMAIL_VERIFICATION, 1440);

            // 3. Cập nhật password_hash thành mã Token (OTP) để làm mật khẩu tạm
            String tokenPasswordHash = hashPassword(token.getToken_value());
            account.setPassword_hash(tokenPasswordHash);

            // Lưu lần 2 để cập nhật mật khẩu Token vào DB
            accountService.updateAccount(account);

            // 4. Gửi Mail
            String subject = "Your Temporary Password";
            String body = "Your temporary password is: " + token.getToken_value();
            try {
                emailService.sendEmail(email, subject, body);
            } catch (Exception e) {
                System.err.println("Lưu DB xong nhưng gửi mail lỗi: " + e.getMessage());
            }
        } else {
            // --- LUỒNG CẬP NHẬT ---
            // Nếu đã có account, cập nhật vai trò + liên kết Entity thông qua binder
            account.setRole(role);                   // ensure role is synchronized
            binder.accept(account);
            accountService.updateAccount(account);
        }
    }

    private String generateOTP() {
        // Simple 6-digit OTP
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    public void resendVerification(String email) throws Exception {
        UserAccount account = accountService.findByUsername(email);
        if (account == null) {
            throw new Exception("Account not found for email: " + email);
        }

        // Thu hồi tất cả các token EMAIL_VERIFICATION cũ của user này
        tokenService.revokeOldTokens(account, TokenType.EMAIL_VERIFICATION);

        // Tạo Token mới (Hết hạn sau 24h = 1440 phút)
        Token token = tokenService.generateToken(account, TokenType.EMAIL_VERIFICATION, 1440);
        
        // Cập nhật mật khẩu tạm thời thành mã Token mới (đã băm)
        String hashedToken = hashPassword(token.getToken_value());
        account.setPassword_hash(hashedToken);
        accountService.updateAccount(account);

        // Gửi Mail thông báo mã xác thực/mật khẩu tạm mới
        String subject = "MIS English Center - Tài khoản của bạn";
        String body = "Chào bạn,\n\n" +
                     "Hệ thống đã cập nhật mã xác thực/mật khẩu tạm thời mới cho tài khoản của bạn.\n" +
                     "Mã của bạn là: " + token.getToken_value() + "\n\n" +
                     "Vui lòng sử dụng mã này để đăng nhập và đổi mật khẩu mới.\n" +
                     "Trân trọng!";
        emailService.sendEmail(email, subject, body);
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}