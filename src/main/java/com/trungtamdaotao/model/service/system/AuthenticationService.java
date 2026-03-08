package com.trungtamdaotao.model.service.system;

import com.trungtamdaotao.model.dao.system.IAccountDAO;
import com.trungtamdaotao.model.entity.enums.TokenType;
import com.trungtamdaotao.model.entity.system.Token;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.service.system.account.AccountService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthenticationService {

    private final AccountService accountService;
    private final TokenService tokenService;

    public AuthenticationService(AccountService accountService, TokenService tokenService) {
        this.accountService = accountService;
        this.tokenService = tokenService;
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

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}