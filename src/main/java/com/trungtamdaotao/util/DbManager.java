package com.trungtamdaotao.util;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class DbManager {
    private static EntityManagerFactory factory;

    // Nạp file .env
    private static final Dotenv dotenv = Dotenv.configure().load();

    public static EntityManagerFactory getFactory() {
        if (factory == null || !factory.isOpen()) {
            // Lấy thông tin từ .env
            Map<String, String> configOverrides = new HashMap<>();
            configOverrides.put("jakarta.persistence.jdbc.url", dotenv.get("DB_URL"));
            configOverrides.put("jakarta.persistence.jdbc.user", dotenv.get("DB_USER"));
            configOverrides.put("jakarta.persistence.jdbc.password", dotenv.get("DB_PASSWORD"));

            // Khởi tạo Factory với các thông số ghi đè
            factory = Persistence.createEntityManagerFactory("TrungTamDaoTaoPU", configOverrides);
        }
        return factory;
    }

    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}