package com.trungtamdaotao.model.service.system.account;

import com.trungtamdaotao.model.entity.enums.TokenType;
import com.trungtamdaotao.model.entity.system.Token;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.service.common.ICommon;
import com.trungtamdaotao.model.service.common.EmailService;
import com.trungtamdaotao.model.service.system.TokenService;
import com.trungtamdaotao.util.EmailConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthenticationService {

    private final AccountService accountService;
    private final TokenService tokenService;
    private final ICommon emailService;

    public AuthenticationService(AccountService accountService, TokenService tokenService) {
        this.accountService = accountService;
        this.tokenService = tokenService;
        this.emailService = new EmailService(EmailConfig.getSmtpHost(), EmailConfig.getSmtpPort(), 
                                           EmailConfig.getMailUsername(), EmailConfig.getMailPassword(), false);
    }

    public LoginResult login(String username, String password) throws Exception {
        UserAccount account = accountService.findByUsername(username);
        if (account == null) {
            throw new Exception("Invalid username");
        }

        String hashedPassword = hashPassword(password);
        if (!hashedPassword.equals(account.getPassword_hash())) {
            throw new Exception("Invalid password");
        }

        boolean needChangePassword = false;

        // If account is inactive, verify with EMAIL_VERIFICATION token
        if (!account.isIs_active()) {
            Token emailVerificationToken = tokenService.findValidTokenByUserAndType(account, TokenType.EMAIL_VERIFICATION);
            if (emailVerificationToken == null || !hashedPassword.equals(hashPassword(emailVerificationToken.getToken_value()))) {
                throw new Exception("Account not verified. Please check your email for verification token.");
            }
            // Activate account
            account.setIs_active(true);
            accountService.updateAccount(account);
            needChangePassword = true; // Need to change password after activation
        }

        return new LoginResult(account, needChangePassword);
    }

    public void forgotPassword(String email) throws Exception {
        UserAccount account = accountService.findByUsername(email);
        if (account == null) {
            throw new Exception("Email not found");
        }

        // Create RESET_PASSWORD token
        Token token = tokenService.generateToken(account, TokenType.PASSWORD_RESET, 15); // 15 minutes

        // Send email with reset link or token
        String subject = "Password Reset Request";
        String body = "Your password reset token is: " + token.getToken_value() + "\nUse this token to reset your password.";
        try {
            emailService.sendEmail(email, subject, body);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new Exception("Failed to send reset email");
        }
    }

    public void resetPassword(String tokenValue, String newPassword) throws Exception {
        Token token = tokenService.findByTokenValue(tokenValue);
        if (token == null || token.isUsed() || token.getExpiry_date().isBefore(java.time.LocalDateTime.now()) || 
            !token.getType().equals(TokenType.PASSWORD_RESET)) {
            throw new Exception("Invalid or expired token");
        }

        UserAccount account = token.getUserAccount();
        accountService.changePassword(account, newPassword);
        tokenService.markAsUsed(tokenValue);
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}