package com.trungtamdaotao.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Tiện ích hỗ trợ thiết kế giao diện Tiếng Việt chuẩn.
 */
public class UIHelper {
    public static final Color PRIMARY_COLOR = Color.decode("#FF6B35");   // Energetic Orange
    public static final Color SECONDARY_COLOR = Color.decode("#D72638"); // Academic Red
    public static final Color ACCENT_COLOR = Color.decode("#2E4057");    // Deep Blue Gray
    public static final Color SUCCESS_COLOR = Color.decode("#27AE60");   // Green
    public static final Color WARNING_COLOR = Color.decode("#F39C12");   // Orange
    public static final Color DANGER_COLOR = Color.decode("#E74C3C");    // Red
    public static final Color BACKGROUND_COLOR = Color.decode("#F5F5F5");
    public static final Color TEXT_COLOR = Color.decode("#333333");
    public static final Color SIDEBAR_COLOR = Color.decode("#2E4057");

    public static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font CARD_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 28);

    public static JButton createSidebarButton(String text, String icon) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(SIDEBAR_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(PRIMARY_COLOR); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(SIDEBAR_COLOR); }
        });
        return btn;
    }

    public static JButton createStandardButton(String text, Color bgColor, String icon) {
        JButton btn = new JButton(icon + " " + text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }

    /**
     * Bổ sung lại phương thức createFormPanel cho tính tương thích ngược.
     */
    public static JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(new LineBorder(PRIMARY_COLOR), title),
            new EmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    public static JPanel createDashboardCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(12, 15, 12, 15)
        ));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(BOLD_FONT); lblTitle.setForeground(Color.GRAY);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(CARD_VALUE_FONT); lblValue.setForeground(color);
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    public static void styleTable(JTable table) {
        table.setFont(MAIN_FONT);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(255, 107, 53, 40));
        table.setSelectionForeground(TEXT_COLOR);
        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD_FONT);
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_COLOR);
        header.setPreferredSize(new Dimension(0, 35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
    }
}
