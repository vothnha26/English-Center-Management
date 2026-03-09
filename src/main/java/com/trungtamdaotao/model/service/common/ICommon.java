package com.trungtamdaotao.model.service.common;

import jakarta.mail.MessagingException;

/**
 * Interface chung cho các dịch vụ thông báo/gửi tin nhắn
 * Có thể mở rộng cho các loại thông báo khác: SMS, notification, v.v.
 */
public interface ICommon {
    /**
     * Gửi email tới địa chỉ được chỉ định
     * 
     * @param to Địa chỉ email nhận
     * @param subject Chủ đề email
     * @param body Nội dung email
     * @throws MessagingException nếu có lỗi gửi email
     */
    void sendEmail(String to, String subject, String body) throws MessagingException;
}
