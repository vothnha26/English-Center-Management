package com.trungtamdaotao.util;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailConfig {
    private static final Dotenv dotenv = Dotenv.configure().load();

    public static String getMailUsername() {
        String username = dotenv.get("MAIL_USERNAME");
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("MAIL_USERNAME không được tìm thấy trong .env file");
        }
        return username;
    }

    public static String getMailPassword() {
        String password = dotenv.get("MAIL_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("MAIL_PASSWORD không được tìm thấy trong .env file");
        }
        return password;
    }

    public static String getSmtpHost() {
        return "smtp.gmail.com";
    }

    public static String getSmtpPort() {
        return "587";
    }
}