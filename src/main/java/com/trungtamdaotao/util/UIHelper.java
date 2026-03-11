package com.trungtamdaotao.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Tiện ích hỗ trợ thiết kế giao diện Tiếng Việt chuẩn.
 */
public class UIHelper {
    public static final Color PRIMARY_COLOR    = Color.decode("#FF6B35");   // Energetic Orange
    public static final Color SECONDARY_COLOR  = Color.decode("#D72638");   // Academic Red
    public static final Color ACCENT_COLOR     = Color.decode("#2E4057");   // Deep Blue Gray
    public static final Color SUCCESS_COLOR    = Color.decode("#27AE60");   // Green
    public static final Color WARNING_COLOR    = Color.decode("#F39C12");   // Orange
    public static final Color DANGER_COLOR     = Color.decode("#E74C3C");   // Red
    public static final Color BACKGROUND_COLOR = Color.decode("#F5F5F5");
    public static final Color TEXT_COLOR       = Color.decode("#333333");
    public static final Color SIDEBAR_COLOR    = Color.decode("#2E4057");

    // -------------------------------------------------------------------------
    // Font — khởi tạo động để chọn font hỗ trợ đầy đủ Unicode/tiếng Việt
    // -------------------------------------------------------------------------
    
    /**
     * Danh sách font ưu tiên hỗ trợ Unicode tiếng Việt.
     * "Segoe UI" đôi khi KHÔNG render tốt tiếng Việt trong Swing.
     * "SansSerif" / "Dialog" là logical font — JVM tự chọn physical font bản địa đúng.
     */
    private static final List<String> PREFERRED_FONTS = Arrays.asList(
        "Be Vietnam Pro", "Noto Sans", "Arial Unicode MS", "Segoe UI", "SansSerif", "Dialog"
    );

    public static final Font MAIN_FONT       = buildFont(Font.PLAIN, 14);
    public static final Font BOLD_FONT       = buildFont(Font.BOLD,  14);
    public static final Font TITLE_FONT      = buildFont(Font.BOLD,  22);
    public static final Font CARD_VALUE_FONT = buildFont(Font.BOLD,  28);

    /** Tìm font đầu tiên có sẵn trong hệ thống để render tiếng Việt đúng. */
    private static Font buildFont(int style, int size) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<String> available = Arrays.asList(ge.getAvailableFontFamilyNames());
        String chosen = PREFERRED_FONTS.stream()
                .filter(f -> available.contains(f) || f.equals("SansSerif") || f.equals("Dialog"))
                .findFirst()
                .orElse("Dialog");
        return new Font(chosen, style, size);
    }

    /**
     * Áp dụng font hỗ trợ tiếng Việt toàn cục cho TẤT CẢ Swing components.
     * Gọi phương thức này TRƯỚC khi tạo bất kỳ component nào (trong main()).
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

    // -------------------------------------------------------------------------
    // UI Factory Methods
    // -------------------------------------------------------------------------

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

    /** Tạo form panel có viền tiêu đề — tương thích ngược với các view cũ. */
    public static JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(new LineBorder(PRIMARY_COLOR), title),
                new EmptyBorder(10, 10, 10, 10)));
        return panel;
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
