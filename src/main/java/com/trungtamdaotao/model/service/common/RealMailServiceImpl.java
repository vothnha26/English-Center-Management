package com.trungtamdaotao.model.service.common;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class RealMailServiceImpl implements IMailService {
    private final String username;
    private final String password;
    private final Properties props;

    public RealMailServiceImpl() {
        // Tận dụng Dotenv từ DbManager của Nhã
        Dotenv dotenv = Dotenv.configure().load();
        this.username = dotenv.get("MAIL_USERNAME"); // Email của Nhã
        this.password = dotenv.get("MAIL_PASSWORD"); // App Password (16 ký tự)

        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    @Override
    public void sendActivationEmail(String toEmail, String otpToken) {
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Kích hoạt tài khoản hệ thống - Trung Tâm Đào Tạo");

            String content = "<h3>Chào mừng bạn đến với hệ thống!</h3>"
                    + "<p>Mã kích hoạt tài khoản của bạn là: <b>" + otpToken + "</b></p>"
                    + "<p>Vui lòng sử dụng mã này để thiết lập mật khẩu mới trong lần đăng nhập đầu tiên.</p>";

            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("=> Đã gửi mail kích hoạt đến: " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("=> Lỗi gửi mail: " + e.getMessage());
        }
    }
}