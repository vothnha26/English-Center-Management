package com.trungtamdaotao.util;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Tiện ích hỗ trợ thiết kế giao diện thống nhất cho toàn hệ thống.
 */
public class UIHelper {
    // Bảng màu chuẩn (Palette - Midnight Blue)
    public static final Color PRIMARY_COLOR    = Color.decode("#2C3E50");   // Midnight Blue
    public static final Color SECONDARY_COLOR  = Color.decode("#D72638");   // Academic Red
    public static final Color ACCENT_COLOR     = Color.decode("#3498DB");   // Bright Blue
    public static final Color SUCCESS_COLOR    = Color.decode("#27AE60");   // Green
    public static final Color WARNING_COLOR    = Color.decode("#F39C12");   // Orange
    public static final Color DANGER_COLOR     = Color.decode("#E74C3C");   // Red
    public static final Color BACKGROUND_COLOR = Color.decode("#ECF0F1");   // Light Gray
    public static final Color TEXT_COLOR       = Color.decode("#2C3E50");
    public static final Color SIDEBAR_COLOR    = Color.decode("#2E4057");

    private static final List<String> PREFERRED_FONTS = Arrays.asList(
        "Segoe UI", "Tahoma", "Arial", "SansSerif"
    );

    public static final Font MAIN_FONT       = buildFont(Font.PLAIN, 14);
    public static final Font BOLD_FONT       = buildFont(Font.BOLD, 14);
    public static final Font TITLE_FONT      = buildFont(Font.BOLD, 18);
    public static final Font CARD_VALUE_FONT = buildFont(Font.BOLD, 28);

    private static Font buildFont(int style, int size) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<String> available = Arrays.asList(ge.getAvailableFontFamilyNames());
        String chosen = PREFERRED_FONTS.stream()
                .filter(available::contains)
                .findFirst()
                .orElse("Dialog");
        return new Font(chosen, style, size);
    }

    /**
     * Áp dụng font hỗ trợ tiếng Việt toàn cục cho TẤT CẢ Swing components.
     */
    public static void applyGlobalFont() {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                javax.swing.plaf.FontUIResource fr = (javax.swing.plaf.FontUIResource) value;
                UIManager.put(key, new javax.swing.plaf.FontUIResource(
                        MAIN_FONT.deriveFont((float) fr.getSize())));
            }
        }
    }

    /**
     * Tạo bộ chọn ngày (DatePicker) với cấu hình chuẩn.
     */
    public static DatePicker createDatePicker() {
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra("yyyy-MM-dd");
        settings.setAllowKeyboardEditing(false);
        
        DatePicker picker = new DatePicker(settings);
        picker.setFont(MAIN_FONT);
        return picker;
    }

    /**
     * Tạo bộ chọn giờ (TimePicker) với cấu hình chuẩn 24h.
     */
    public static TimePicker createTimePicker() {
        TimePickerSettings settings = new TimePickerSettings();
        settings.use24HourClockFormat();
        settings.setAllowKeyboardEditing(false);
        
        TimePicker picker = new TimePicker(settings);
        picker.setFont(MAIN_FONT);
        return picker;
    }

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
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(SIDEBAR_COLOR); }
        });
        return btn;
    }

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

    public static JPanel createDashboardCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(12, 15, 12, 15)));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(BOLD_FONT);
        lblTitle.setForeground(Color.GRAY);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(CARD_VALUE_FONT);
        lblValue.setForeground(color);
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    /** Tạo form panel có viền tiêu đề. */
    public static JPanel createFormPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(new LineBorder(PRIMARY_COLOR), title),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    public static void styleTable(JTable table) {
        table.setFont(MAIN_FONT);
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(52, 152, 219, 100));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);

        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 35));

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
}
