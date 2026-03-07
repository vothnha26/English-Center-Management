package com.trungtamdaotao.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hàm mã hóa mật khẩu
    public static String hashPassword(String plainTextPassword) {
        // gensalt() tạo ra một chuỗi ngẫu nhiên để trộn vào mật khẩu
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // Hàm kiểm tra mật khẩu khi đăng nhập
    public static boolean checkPassword(String plainText, String hashed) {
        return BCrypt.checkpw(plainText, hashed);
    }
}