package com.trungtamdaotao.view.common;

import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.util.security.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Lớp cơ sở cho các Frame quản lý (Manager Frames).
 * Thống nhất về cấu trúc Layout, phong cách hiển thị và Phân quyền.
 */
public abstract class BaseManagerFrame extends JFrame {

    public BaseManagerFrame(String title) {
        this(title, new AccountRole[]{AccountRole.Admin, AccountRole.Staff}, null);
    }

    public BaseManagerFrame(String title, AccountRole[] allowedAccountRoles, StaffRole[] allowedStaffRoles) {
        // Kiểm tra quyền truy cập
        if (!checkAccess(allowedAccountRoles, allowedStaffRoles)) {
            JOptionPane.showMessageDialog(null, 
                "Bạn không có quyền truy cập chức năng này!", 
                "Truy cập bị từ chối", 
                JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }

        setTitle(title + " - MIS Language Center");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UIHelper.BACKGROUND_COLOR);

        // Các bước khởi tạo chuẩn
        initComponents();
        handleEvents();
        loadTableData();

        // Cấu hình cửa sổ
        setSize(1300, 750);
        setLocationRelativeTo(null);
    }

    private boolean checkAccess(AccountRole[] allowedAccountRoles, StaffRole[] allowedStaffRoles) {
        if (UserSession.getCurrentUser() == null) return false;
        
        // Admin luôn có quyền
        if (UserSession.isAdmin()) return true;

        // Kiểm tra AccountRole (Lớp 1)
        AccountRole userAccRole = UserSession.getCurrentUser().getRole();
        boolean accRoleMatch = Arrays.stream(allowedAccountRoles)
                                     .anyMatch(r -> r == userAccRole);
        
        if (!accRoleMatch) return false;

        // Nếu là Staff, kiểm tra tiếp StaffRole (Lớp 2)
        if (UserSession.isStaff() && allowedStaffRoles != null && allowedStaffRoles.length > 0) {
            StaffRole userStaffRole = UserSession.getStaffRole();
            return Arrays.stream(allowedStaffRoles)
                         .anyMatch(r -> r == userStaffRole);
        }

        return true;
    }

    /** Khởi tạo các thành phần giao diện */
    protected abstract void initComponents();

    /** Đăng ký các sự kiện (ActionListener, MouseListener...) */
    protected abstract void handleEvents();

    /** Nạp dữ liệu vào bảng */
    protected abstract void loadTableData();

    protected JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIHelper.BOLD_FONT);
        label.setForeground(UIHelper.TEXT_COLOR);
        return label;
    }

    protected void setupTable(JTable table) {
        UIHelper.styleTable(table);
    }
}
