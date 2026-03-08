package com.trungtamdaotao.model.service.system;

import com.trungtamdaotao.model.dao.impl.AccountDAOImpl;
import com.trungtamdaotao.model.dao.impl.TokenDAOImpl;
import com.trungtamdaotao.model.dao.system.IAccountDAO;
import com.trungtamdaotao.model.dao.system.ITokenDAO;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.TokenType;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.entity.system.Token;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.service.system.account.AccountService;
import com.trungtamdaotao.util.DbManager;
import com.trungtamdaotao.util.EmailConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
                             Teacher teacher, Student student, Staff staff) throws Exception {
        // Check if account already exists
        UserAccount account = accountService.findByUsername(username);

        if (account == null) {
            // Create account with temporary password first
            String tempPassword = "temp";
            String tempPasswordHash = hashPassword(tempPassword);
            account = accountService.createAccount(username, tempPasswordHash, role, teacher, student, staff);

            // Generate token for email verification
            Token token = tokenService.generateToken(account, TokenType.EMAIL_VERIFICATION, 15); // 15 minutes expiry

            // Update account password to hashed token value
            String tokenPasswordHash = hashPassword(token.getToken_value());
            account.setPassword_hash(tokenPasswordHash);
            accountService.updateAccount(account);

            // Send email with token as temporary password
            String subject = "Your Temporary Password";
            String body = "Your temporary password is: " + token.getToken_value();
            try {
                emailService.sendEmail(email, subject, body);
            } catch (Exception e) {
                System.err.println("Lỗi gửi email: " + e.getMessage());
            }
        } else {
            // Update existing account with new entity relationships if needed
            if (teacher != null) account.setTeacher(teacher);
            if (student != null) account.setStudent(student);
            if (staff != null) account.setStaff(staff);
        }
    }

    private String generateOTP() {
        // Simple 6-digit OTP
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    public void resendVerification(String email) throws Exception {
        UserAccount account = accountService.findByUsername(email);
        if (account == null) {
            throw new Exception("Account not found");
        }
        if (account.isIs_active()) {
            throw new Exception("Account already active");
        }

        // Create new EMAIL_VERIFICATION token
        Token token = tokenService.generateToken(account, TokenType.EMAIL_VERIFICATION, 1440);
        // Update password to new token
        String hashedToken = hashPassword(token.getToken_value());
        account.setPassword_hash(hashedToken);
        accountService.updateAccount(account);

        // Send email
        String subject = "Email Verification";
        String body = "Your verification token is: " + token.getToken_value();
        emailService.sendEmail(email, subject, body);
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}