package com.trungtamdaotao.util;

import com.trungtamdaotao.model.entity.system.UserAccount;

public class GlobalSession {
    private static UserAccount currentUser;

    public static void login(UserAccount user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static UserAccount getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}