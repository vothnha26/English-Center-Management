package com.trungtamdaotao.util;

import java.security.SecureRandom;

public class TokenGenerator {
    // Tạo mã OTP 6 số ngẫu nhiên
    public static String generateOTP() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}