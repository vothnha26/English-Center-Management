package com.trungtamdaotao.model.service.common;

import com.trungtamdaotao.util.EmailConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý gửi Feedback qua Email với mẫu HTML.
 */
public class EmailFeedbackService {
    
    private List<EmailTask> emailQueue = new ArrayList<>();

    public void addToQueue(String toEmail, String studentName, String className, String score, String grade, String comment) {
        String htmlBody = String.format(
            "<html><body style='font-family: Segoe UI, sans-serif;'>" +
            "<h2 style='color: #FF6B35;'>THÔNG BÁO KẾT QUẢ HỌC TẬP</h2>" +
            "<p>Xin chào <b>%s</b>,</p>" +
            "<p>Trung tâm MIS English Center xin gửi tới bạn kết quả khóa học:</p>" +
            "<table border='1' style='border-collapse: collapse; width: 100%%;'>" +
            "<tr><td style='padding: 8px; background: #f2f2f2;'><b>Lớp học</b></td><td style='padding: 8px;'>%s</td></tr>" +
            "<tr><td style='padding: 8px; background: #f2f2f2;'><b>Điểm số</b></td><td style='padding: 8px;'>%s</td></tr>" +
            "<tr><td style='padding: 8px; background: #f2f2f2;'><b>Xếp loại</b></td><td style='padding: 8px;'>%s</td></tr>" +
            "</table>" +
            "<p><b>Nhận xét của giáo viên:</b> %s</p>" +
            "<p>Trân trọng,<br>Ban Đào Tạo MIS Center</p>" +
            "</body></html>",
            studentName, className, score, grade, comment
        );
        
        emailQueue.add(new EmailTask(toEmail, "Kết quả học tập - " + className, htmlBody));
    }

    public void processQueue() {
        // Sử dụng Java Lambda để mô phỏng việc gửi hàng loạt
        emailQueue.forEach(task -> {
            System.out.println(">>> Đang gửi Email tới: " + task.to);
            // Thực tế sẽ gọi EmailConfig.sendEmail(task.to, task.subject, task.body);
        });
        emailQueue.clear();
    }

    private static class EmailTask {
        String to, subject, body;
        EmailTask(String to, String subject, String body) {
            this.to = to; this.subject = subject; this.body = body;
        }
    }
}
