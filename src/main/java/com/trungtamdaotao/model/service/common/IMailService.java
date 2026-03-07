package com.trungtamdaotao.model.service.common;

public interface IMailService {
    void sendActivationEmail(String toEmail, String otpToken);
}
