package com.trungtamdaotao.view;

import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.util.DashboardChart;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.view.student.StudentManagerPanel;
import com.trungtamdaotao.view.teacher.TeacherManagerPanel;
import com.trungtamdaotao.view.academic.AttendanceManagerPanel;
import com.trungtamdaotao.view.academic.ScheduleManagerPanel;
import com.trungtamdaotao.view.academic.AcademicManagerPanel;
import com.trungtamdaotao.view.academic.GradeManagerPanel;
import com.trungtamdaotao.view.academic.TeacherDashboardPanel;
import com.trungtamdaotao.view.system.StaffManagerPanel;
import com.trungtamdaotao.view.system.PermissionManagerPanel;
import com.trungtamdaotao.view.finance.InvoiceManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * Main Frame Hoàn thiện - Hỗ trợ Phân quyền Vai trò (Staff vs Teacher).
 */
public class MainMenuFrame extends JFrame {

    private JPanel pnlSidebar, pnlContent;
    private JLabel lblPageTitle;
    private CardLayout cardLayout;
    private AccountRole currentRole;
    private boolean sidebarCollapsed = false;
    private final int SIDEBAR_WIDTH = 280;
    private final int SIDEBAR_COLLAPSED_WIDTH = 70;

    public MainMenuFrame(AccountRole role) {
        this.currentRole = role;
        setTitle("HỆ THỐNG QUẢN LÝ TRUNG TÂM ANH NGỮ - [" + role + "]");
        setSize(1450, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    public MainMenuFrame() {
        this(AccountRole.ADMIN);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Sidebar
        pnlSidebar = createSidebar();
        add(pnlSidebar, BorderLayout.WEST);

        // Main Area
        JPanel pnlMainArea = new JPanel(new BorderLayout());
        pnlMainArea.setBackground(UIHelper.BACKGROUND_COLOR);
        pnlMainArea.add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        pnlContent.setOpaque(false);
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Register Panels
        if (currentRole == AccountRole.TEACHER) {
            pnlContent.add(new TeacherDashboardPanel(), "Dashboard");
        } else {
            pnlContent.add(createDashboardPanel(), "Dashboard");
        }
        
        pnlContent.add(new StudentManagerPanel(), "Students");
        pnlContent.add(new TeacherManagerPanel(), "Teachers");
        pnlContent.add(new AcademicManagerPanel(), "Academic");
        pnlContent.add(new ScheduleManagerPanel(), "Schedules");
        pnlContent.add(new AttendanceManagerPanel(), "Attendance");
        pnlContent.add(new GradeManagerPanel(), "Grades");
        pnlContent.add(new InvoiceManagerPanel(), "Finance");
        pnlContent.add(new StaffManagerPanel(), "Staff");
        pnlContent.add(new PermissionManagerPanel(), "Permissions");

        pnlMainArea.add(pnlContent, BorderLayout.CENTER);
        add(pnlMainArea, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new MigLayout("wrap 1, inset 0, fillx", "[fill]", "[]20[]2[]10[]2[]2[]2[]2[]2[]2[]10[]2[]2[]2[]push[]20"));
        sidebar.setBackground(UIHelper.SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        JLabel lblLogo = new JLabel("MIS CENTER");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBorder(new EmptyBorder(30, 25, 20, 25));
        sidebar.add(lblLogo);

        sidebar.add(createSidebarButton("TỔNG QUAN", "Dashboard", "\u25A3"));

        sidebar.add(createGroupLabel("QUẢN LÝ HỌC VỤ"));
        if (currentRole != AccountRole.TEACHER) {
            sidebar.add(createSidebarButton("Học viên", "Students", "\u25B8"));
            sidebar.add(createSidebarButton("Giáo viên", "Teachers", "\u25B8"));
            sidebar.add(createSidebarButton("Khóa & Lớp học", "Academic", "\u25B8"));
        }
        sidebar.add(createSidebarButton("Lịch dạy/học", "Schedules", "\u25B8"));
        sidebar.add(createSidebarButton("Điểm danh", "Attendance", "\u25B8"));
        sidebar.add(createSidebarButton("Quản lý Điểm số", "Grades", "\u270E"));

        if (currentRole != AccountRole.TEACHER) {
            sidebar.add(createGroupLabel("TÀI CHÍNH & HỆ THỐNG"));
            sidebar.add(createSidebarButton("Hóa đơn", "Finance", "\u25B8"));
            sidebar.add(createSidebarButton("Nhân sự", "Staff", "\u25B8"));
            sidebar.add(createSidebarButton("Phân quyền", "Permissions", "\u25B8"));
        }

        JButton btnLogout = UIHelper.createSidebarButton("ĐĂNG XUẤT", "\u2716");
        btnLogout.addActionListener(e -> System.exit(0));
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JLabel createGroupLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(255, 255, 255, 100));
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

        JButton btnToggle = new JButton("\u2630");
        btnToggle.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
        btnToggle.setBorderPainted(false);
        btnToggle.setContentAreaFilled(false);
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggle.addActionListener(e -> toggleSidebar());
        
        JPanel pnlLeftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        pnlLeftHeader.setOpaque(false);
        pnlLeftHeader.add(btnToggle);
        lblPageTitle = new JLabel("TỔNG QUAN HỆ THỐNG");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlLeftHeader.add(lblPageTitle);
        header.add(pnlLeftHeader, BorderLayout.WEST);

        String roleName = switch (currentRole) {
            case ADMIN -> "Quản trị viên";
            case TEACHER -> "Giáo viên";
            case STAFF -> "Nhân viên";
            case STUDENT -> "Học viên";
            default -> currentRole.toString();
        };
        JLabel lblUser = new JLabel("Xin chào, " + roleName + " ");
        lblUser.setFont(UIHelper.MAIN_FONT);
        lblUser.setBorder(new EmptyBorder(0, 0, 0, 30));
        header.add(lblUser, BorderLayout.EAST);

        return header;
    }

    private void toggleSidebar() {
        sidebarCollapsed = !sidebarCollapsed;
        pnlSidebar.setPreferredSize(new Dimension(sidebarCollapsed ? 70 : SIDEBAR_WIDTH, 0));
        pnlSidebar.revalidate();
        pnlSidebar.repaint();
    }

    private JPanel createDashboardPanel() {
        JPanel pnlDashboard = new JPanel(new MigLayout("wrap 3, fillx, insets 0", "[fill, grow]", "[]25[]25[grow]"));
        pnlDashboard.setOpaque(false);
        
        pnlDashboard.add(UIHelper.createDashboardCard("DOANH THU THÁNG", "1.250.000.000 VNĐ", UIHelper.SUCCESS_COLOR), "grow");
        pnlDashboard.add(UIHelper.createDashboardCard("HỌC VIÊN MỚI", "+124 học viên", UIHelper.PRIMARY_COLOR), "grow");
        pnlDashboard.add(UIHelper.createDashboardCard("LỚP SẮP MỞ", "12 lớp học", UIHelper.SECONDARY_COLOR), "grow");

        JPanel pnlChart1 = new JPanel(new BorderLayout());
        pnlChart1.setBackground(Color.WHITE);
        pnlChart1.add(new DashboardChart("TĂNG TRƯỞNG HỌC VIÊN", Arrays.asList(40, 65, 55, 90, 110, 124), UIHelper.PRIMARY_COLOR));
        
        JPanel pnlChart2 = new JPanel(new BorderLayout());
        pnlChart2.setBackground(Color.WHITE);
        pnlChart2.add(new DashboardChart("BIẾN ĐỘNG HỌC PHÍ", Arrays.asList(800, 950, 1100, 1050, 1200, 1250), UIHelper.SUCCESS_COLOR));

        pnlDashboard.add(pnlChart1, "span 2, grow, height 400");
        pnlDashboard.add(pnlChart2, "grow, height 400");
        return pnlDashboard;
    }

    public static void main(String[] args) {
        com.formdev.flatlaf.FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new MainMenuFrame(AccountRole.TEACHER).setVisible(true));
    }
}
