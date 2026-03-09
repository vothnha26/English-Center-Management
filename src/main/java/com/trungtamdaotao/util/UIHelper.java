package com.trungtamdaotao.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Tiện ích hỗ trợ thiết kế giao diện thống nhất cho toàn hệ thống.
 */
public class UIHelper {
    // Bảng màu chuẩn (Palette)
    public static final Color PRIMARY_COLOR = Color.decode("#2C3E50");   // Midnight Blue
    public static final Color SUCCESS_COLOR = Color.decode("#27AE60");   // Green
    public static final Color WARNING_COLOR = Color.decode("#F39C12");   // Orange
    public static final Color DANGER_COLOR = Color.decode("#E74C3C");    // Red
    public static final Color BACKGROUND_COLOR = Color.decode("#ECF0F1"); // Light Gray
    public static final Color TEXT_COLOR = Color.decode("#2C3E50");

    // Font chuẩn
    public static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);

    /**
     * Tạo nút bấm chuẩn với màu sắc và icon.
     */
    public static JButton createStandardButton(String text, Color bgColor, String iconUnicode) {
        JButton btn = new JButton(iconUnicode + " " + text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Định dạng JTable theo chuẩn (Zebra striping, Header style).
     */
    public static void styleTable(JTable table) {
        table.setFont(MAIN_FONT);
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(52, 152, 219, 100));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);

        // Header style
        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 35));

        // Zebra striping
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean cellHasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, cellHasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 244, 244));
                }
                setBorder(noFocusBorder);
                return c;
            }
        });
    }

    /**
     * Tạo Panel chứa Form với GridBagLayout chuẩn.
     */
    public static JPanel createFormPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR), title),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }
}
