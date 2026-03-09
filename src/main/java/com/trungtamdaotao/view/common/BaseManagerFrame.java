package com.trungtamdaotao.view.common;

import com.trungtamdaotao.util.UIHelper;
import javax.swing.*;
import java.awt.*;

/**
 * Lớp cơ sở cho các Frame quản lý (Manager Frames).
 * Thống nhất về cấu trúc Layout và phong cách hiển thị.
 */
public abstract class BaseManagerFrame extends JFrame {

    public BaseManagerFrame(String title) {
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
