package com.trungtamdaotao;

import com.trungtamdaotao.util.AuthContext;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.view.system.PersonnelManagementFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // 1. Giả lập đăng nhập với quyền ADMIN để có toàn quyền test CRUD
        AuthContext.setCurrentRole(AccountRole.Admin);

        // 2. Cài đặt giao diện nhìn cho giống Windows/MacOS (tùy chọn)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Chạy giao diện trên luồng Event Dispatch Thread (EDT) của Swing
        SwingUtilities.invokeLater(() -> {
            PersonnelManagementFrame frame = new PersonnelManagementFrame();
            frame.setVisible(true);
        });
    }
}