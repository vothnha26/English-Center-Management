package com.trungtamdaotao.model.service.system;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private final String smtpHost;
    private final String smtpPort;
    private final String username;
    private final String password;
    private final boolean mockMode; // Mock mode: chỉ print email ra console

    public EmailService(String smtpHost, String smtpPort, String username, String password) {
        this(smtpHost, smtpPort, username, password, false);
    }

    public EmailService(String smtpHost, String smtpPort, String username, String password, boolean mockMode) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.password = password;
        this.mockMode = mockMode;
    }

    /** Constructor mặc định dùng mock mode để test */
    public EmailService() {
        this("smtp.gmail.com", "587", "your-email@gmail.com", "your-password", true);
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        if (mockMode) {
            // Chế độ mock: chỉ in ra console, không gửi thực
            System.out.println("========== EMAIL (MOCK MODE) ==========");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            System.out.println("========================================");
            return;
        }

        // Gửi email thực
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
}