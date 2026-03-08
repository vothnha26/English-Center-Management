package com.trungtamdaotao.model.service.system;

import com.trungtamdaotao.model.dao.system.ITokenDAO;
import com.trungtamdaotao.model.entity.enums.TokenType;
import com.trungtamdaotao.model.entity.system.Token;
import com.trungtamdaotao.model.entity.system.UserAccount;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

public class TokenService {

    private final ITokenDAO tokenDAO;

    public TokenService(ITokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    public Token generateToken(UserAccount userAccount, TokenType type, int expiryMinutes) {
        String tokenValue = generateRandomToken();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(expiryMinutes);

        Token token = new Token();
        token.setToken_value(tokenValue);
        token.setUserAccount(userAccount);
        token.setExpiry_date(expiry);
        token.setType(type);
        token.setUsed(false);

        tokenDAO.save(token);
        return token;
    }

    public Token findByTokenValue(String tokenValue) {
        return tokenDAO.findByTokenValue(tokenValue);
    }

    public boolean isValidToken(String tokenValue) {
        Token token = findByTokenValue(tokenValue);
        return token != null && !token.isUsed() && token.getExpiry_date().isAfter(LocalDateTime.now());
    }

    public void markAsUsed(String tokenValue) {
        tokenDAO.markAsUsed(tokenValue);
    }

    public Token findValidTokenByUserAndType(UserAccount userAccount, TokenType type) {
        return tokenDAO.findValidTokenByUserAndType(userAccount, type);
    }

    private String generateRandomToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}