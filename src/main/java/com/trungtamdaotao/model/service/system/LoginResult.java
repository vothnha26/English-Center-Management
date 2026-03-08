package com.trungtamdaotao.model.service.system;

import com.trungtamdaotao.model.entity.system.UserAccount;

public class LoginResult {
    private final UserAccount account;
    private final boolean needChangePassword;

    public LoginResult(UserAccount account, boolean needChangePassword) {
        this.account = account;
        this.needChangePassword = needChangePassword;
    }

    public UserAccount getAccount() {
        return account;
    }

    public boolean isNeedChangePassword() {
        return needChangePassword;
    }
}