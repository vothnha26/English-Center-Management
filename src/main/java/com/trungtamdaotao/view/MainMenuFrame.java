package com.trungtamdaotao.view;

import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.student.StudentManagerPanel;
import com.trungtamdaotao.view.teacher.TeacherManagerPanel;
import com.trungtamdaotao.view.academic.AttendanceManagerPanel;
import com.trungtamdaotao.view.academic.ScheduleManagerPanel;
import com.trungtamdaotao.view.system.StaffManagerPanel;
import com.trungtamdaotao.view.finance.InvoiceManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Frame Hoàn thiện - Single Window Architecture.
 */
public class MainMenuFrame extends JFrame {

    private JPanel pnlContent;
    private JLabel lblPageTitle;
    private CardLayout cardLayout;

    public MainMenuFrame() {
        setTitle("HỆ THỐNG QUẢN LÝ TRUNG TÂM ANH NGỮ");
        setSize(1450, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // --- Sidebar ---
        add(createSidebar(), BorderLayout.WEST);

        // --- Main Area ---
        JPanel pnlMainArea = new JPanel(new BorderLayout());
        pnlMainArea.setBackground(UIHelper.BACKGROUND_COLOR);

        pnlMainArea.add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        pnlContent.setOpaque(false);
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Đăng ký toàn bộ Panel
        pnlContent.add(createDashboardPanel(), "Dashboard");
        pnlContent.add(new StudentManagerPanel(), "Students");
        pnlContent.add(new TeacherManagerPanel(), "Teachers");
        pnlContent.add(new ScheduleManagerPanel(), "Schedules");
        pnlContent.add(new AttendanceManagerPanel(), "Attendance");
        pnlContent.add(new StaffManagerPanel(), "Staff");
        pnlContent.add(new InvoiceManagerPanel(), "Finance");

        pnlMainArea.add(pnlContent, BorderLayout.CENTER);
        add(pnlMainArea, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new MigLayout("wrap 1, inset 0, fillx", "[fill]", "[]20[]2[]10[]2[]2[]2[]2[]10[]2[]2[]push[]20"));
        sidebar.setBackground(UIHelper.SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(280, 0));

        JLabel lblLogo = new JLabel("MIS ENGLISH CENTER");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBorder(new EmptyBorder(30, 25, 20, 25));
        sidebar.add(lblLogo);

        sidebar.add(createSidebarButton("TỔNG QUAN", "Dashboard", "\u25A3"));

        sidebar.add(createGroupLabel("QUẢN LÝ HỌC VỤ"));
        sidebar.add(createSidebarButton("Học viên", "Students", "\u25B8"));
        sidebar.add(createSidebarButton("Giáo viên", "Teachers", "\u25B8"));
        sidebar.add(createSidebarButton("Lịch học", "Schedules", "\u25B8"));
        sidebar.add(createSidebarButton("Điểm danh", "Attendance", "\u25B8"));

        sidebar.add(createGroupLabel("TÀI CHÍNH & HỆ THỐNG"));
        sidebar.add(createSidebarButton("Hóa đơn", "Finance", "\u25B8"));
        sidebar.add(createSidebarButton("Nhân sự", "Staff", "\u25B8"));

        JButton btnLogout = UIHelper.createSidebarButton("ĐĂNG XUẤT", "\u2716");
        btnLogout.addActionListener(e -> System.exit(0));
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JLabel createGroupLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(255, 255, 255, 120));
        lbl.setBorder(new EmptyBorder(15, 25, 5, 25));
        return lbl;
    }

    private JButton createSidebarButton(String text, String cardName, String icon) {
        JButton btn = UIHelper.createSidebarButton(text, icon);
        btn.addActionListener(e -> {
            lblPageTitle.setText(text.toUpperCase());
            cardLayout.show(pnlContent, cardName);
        });
        return btn;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        lblPageTitle = new JLabel("TỔNG QUAN HỆ THỐNG");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPageTitle.setForeground(UIHelper.TEXT_COLOR);
        lblPageTitle.setBorder(new EmptyBorder(0, 30, 0, 0));
        header.add(lblPageTitle, BorderLayout.WEST);

        JLabel lblUser = new JLabel("Xin chào, Quản trị viên ");
        lblUser.setFont(UIHelper.MAIN_FONT);
        lblUser.setBorder(new EmptyBorder(0, 0, 0, 30));
        header.add(lblUser, BorderLayout.EAST);

        return header;
    }

    private JPanel createDashboardPanel() {
        JPanel pnlDashboard = new JPanel(new MigLayout("wrap 3, fillx, insets 0", "[fill, grow]", "[]25[]"));
        pnlDashboard.setOpaque(false);
        pnlDashboard.add(UIHelper.createDashboardCard("DOANH THU THÁNG", "1.250.000.000 VNĐ", UIHelper.SUCCESS_COLOR), "grow");
        pnlDashboard.add(UIHelper.createDashboardCard("HỌC VIÊN MỚI", "+124 học viên", UIHelper.PRIMARY_COLOR), "grow");
        pnlDashboard.add(UIHelper.createDashboardCard("LỚP SẮP MỞ", "12 lớp học", UIHelper.SECONDARY_COLOR), "grow");
        return pnlDashboard;
    }

    public static void main(String[] args) {
        com.formdev.flatlaf.FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
