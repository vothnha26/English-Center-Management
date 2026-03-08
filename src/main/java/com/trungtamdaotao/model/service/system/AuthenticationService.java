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

    public UserAccount login(String username, String password) throws Exception {
        UserAccount account = accountService.findByUsername(username);
        if (account == null || !account.isIs_active()) {
            throw new Exception("Invalid username or account inactive");
        }

        String hashedPassword = hashPassword(password);
        if (!hashedPassword.equals(account.getPassword_hash())) {
            throw new Exception("Invalid password");
        }

        // If first login, mark EMAIL_VERIFICATION token as used
        if (account.isIs_first_login()) {
            Token emailVerificationToken = tokenService.findValidTokenByUserAndType(account, TokenType.EMAIL_VERIFICATION);
            if (emailVerificationToken != null) {
                tokenService.markAsUsed(emailVerificationToken.getToken_value());
            }
            account.setIs_first_login(false);
            accountService.updateAccount(account);
        }

        return account;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}