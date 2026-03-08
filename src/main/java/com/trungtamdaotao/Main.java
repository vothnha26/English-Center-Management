package com.trungtamdaotao;

import com.trungtamdaotao.controller.system.LoginController;
import com.trungtamdaotao.model.dao.system.IUserAccountDAO;
import com.trungtamdaotao.model.dao.impl.UserAccountDAOImpl;
import com.trungtamdaotao.model.service.common.IMailService;
import com.trungtamdaotao.model.service.common.RealMailServiceImpl;
import com.trungtamdaotao.model.service.system.account.AuthService;
import com.trungtamdaotao.view.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // 1. Khởi tạo tầng DAO
        IUserAccountDAO accountDAO = new UserAccountDAOImpl();

        // 2. Khởi tạo tầng Service (Sử dụng Gmail thật)
        IMailService mailService = new RealMailServiceImpl();
        AuthService authService = new AuthService(accountDAO, mailService);

        // 3. Khởi tạo View (Giao diện)
        LoginFrame loginFrame = new LoginFrame();

        // 4. Khởi tạo Controller để "kết nối" View và Service
        new LoginController(loginFrame, authService);

        // 5. Hiển thị
        loginFrame.setVisible(true);
    }
}