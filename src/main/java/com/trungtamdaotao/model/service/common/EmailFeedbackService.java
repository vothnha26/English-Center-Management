package com.trungtamdaotao.model.service.common;

import com.trungtamdaotao.util.EmailConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * Service xử lý gửi Feedback qua Email với mẫu HTML.
 */
public class EmailFeedbackService {
    
    private final EmailService emailService;
    private List<EmailTask> emailQueue = new ArrayList<>();

    public EmailFeedbackService() {
        // Khởi tạo EmailService từ cấu hình EmailConfig
        this.emailService = new EmailService(
            EmailConfig.getSmtpHost(),
            EmailConfig.getSmtpPort(),
            EmailConfig.getMailUsername(),
            EmailConfig.getMailPassword(),
            false // Tắt mock mode để gửi thật
        );
    }

    public void addToQueue(String toEmail, String studentName, String className, String score, String grade, String comment) {
        String htmlBody = String.format(
            "<html><body style='font-family: Segoe UI, sans-serif;'>" +
            "<h2 style='color: #2C3E50;'>THÔNG BÁO KẾT QUẢ HỌC TẬP</h2>" +
            "<p>Xin chào <b>%s</b>,</p>" +
            "<p>Trung tâm <b>MIS English Center</b> xin gửi tới bạn kết quả khóa học:</p>" +
            "<table border='1' style='border-collapse: collapse; width: 100%%; max-width: 500px;'>" +
            "<tr><td style='padding: 10px; background: #f2f2f2; width: 150px;'><b>Lớp học</b></td><td style='padding: 10px;'>%s</td></tr>" +
            "<tr><td style='padding: 10px; background: #f2f2f2;'><b>Điểm số</b></td><td style='padding: 10px;'>%s</td></tr>" +
            "<tr><td style='padding: 10px; background: #f2f2f2;'><b>Xếp loại</b></td><td style='padding: 10px;'>%s</td></tr>" +
            "</table>" +
            "<p style='margin-top: 20px;'><b>Nhận xét của giáo viên:</b><br/>%s</p>" +
            "<hr style='border: none; border-top: 1px solid #eee; margin-top: 30px;' />" +
            "<p style='color: #7f8c8d; font-size: 12px;'>Đây là email tự động từ hệ thống quản lý đào tạo MIS. Vui lòng không trả lời email này.</p>" +
            "</body></html>",
            studentName, className, score, grade, comment
        );
        
        emailQueue.add(new EmailTask(toEmail, "Kết quả học tập lớp " + className + " - MIS English Center", htmlBody));
    }

    public void processQueue() throws Exception {
        for (EmailTask task : emailQueue) {
            System.out.println(">>> Đang gửi Email tới: " + task.to);
            // Gửi nội dung HTML bằng phương thức sendHtmlEmail (cần bổ sung vào EmailService hoặc dùng sendEmail)
            emailService.sendEmail(task.to, task.subject, task.body);
        }
        emailQueue.clear();
    }

    /**
     * Gửi email ngay lập tức cho một học viên
     */
    public void sendImmediate(String toEmail, String studentName, String className, String score, String grade, String comment) throws Exception {
        addToQueue(toEmail, studentName, className, score, grade, comment);
        processQueue();
    }

    private static class EmailTask {
        String to, subject, body;
        EmailTask(String to, String subject, String body) {
            this.to = to; this.subject = subject; this.body = body;
        }
    }
}
