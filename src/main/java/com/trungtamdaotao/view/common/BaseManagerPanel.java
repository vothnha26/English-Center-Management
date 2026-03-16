package com.trungtamdaotao.view.common;

import com.trungtamdaotao.util.UIHelper;
import javax.swing.*;
import java.awt.*;

/**
 * Lớp cơ sở cho các Panel quản lý (Manager Panels).
 * Thay thế cho BaseManagerFrame để tích hợp vào CardLayout của MainMenuFrame.
 */
public abstract class BaseManagerPanel extends JPanel {

    public BaseManagerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BACKGROUND_COLOR);

        // Các bước khởi tạo chuẩn
        initComponents();
        handleEvents();
        loadTableData();
    }

    /** Khởi tạo các thành phần giao diện */
    protected abstract void initComponents();

    /** Đăng ký các sự kiện (ActionListener, MouseListener...) */
    protected abstract void handleEvents();

    /** Nạp dữ liệu vào bảng */
    protected abstract void loadTableData();

    /**
     * Tiện ích tạo Label chuẩn cho Form.
     */
    protected JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIHelper.BOLD_FONT);
        label.setForeground(UIHelper.TEXT_COLOR);
        return label;
    }

    /**
     * Tiện ích cấu hình JTable.
     */
    protected void setupTable(JTable table) {
        UIHelper.styleTable(table);
    }
}
