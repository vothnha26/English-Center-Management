package com.trungtamdaotao.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class DbTester {
    public static void main(String[] args) {
        System.out.println("--- 🔍 Đang kiểm tra kết nối Database qua DbManager ---");

        // Khai báo Factory để kiểm tra null
        EntityManagerFactory factory = null;
        EntityManager em = null;

        try {
            // 1. Lấy Factory (Lúc này DbManager sẽ nạp file .env)
            factory = DbManager.getFactory();

            // 2. Tạo EntityManager
            em = factory.createEntityManager();

            if (em != null && em.isOpen()) {
                System.out.println("✅ KẾT NỐI THÀNH CÔNG!");
                System.out.println("🚀 Hibernate đã kết nối tới MySQL và sẵn sàng làm việc.");

                // Thử in ra một thông tin nhỏ để chắc chắn metadata đã load
                System.out.println("📌 Persistence Unit: " + factory.getProperties().get("hibernate.ejb.persistenceUnitName"));
            }
        } catch (Exception e) {
            System.err.println("❌ KẾT NỐI THẤT BẠI!");
            System.err.println("⚠️ Lỗi chi tiết: " + e.getMessage());

            // Gợi ý thông minh dựa trên lỗi
            if (e.getMessage().contains("Access denied")) {
                System.err.println("👉 Gợi ý: Sai User hoặc Password trong file .env");
            } else if (e.getMessage().contains("Communications link failure")) {
                System.err.println("👉 Gợi ý: Chưa bật MySQL Server (XAMPP/MySQL Service)");
            } else {
                System.err.println("👉 Gợi ý: Kiểm tra file .env, persistence.xml hoặc MySQL Driver.");
            }
        } finally {
            // Đóng EntityManager trước
            if (em != null && em.isOpen()) {
                em.close();
            }
            // Đóng Factory để giải phóng tài nguyên hoàn toàn
            DbManager.close();
            System.out.println("--- 🏁 Kết thúc kiểm tra ---");
        }
    }
}