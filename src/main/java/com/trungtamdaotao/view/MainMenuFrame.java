package com.trungtamdaotao.view;

import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.util.security.IPermission;
import com.trungtamdaotao.util.security.UserSession;
import com.trungtamdaotao.view.academic.AttendanceManagerFrame;
import com.trungtamdaotao.view.academic.ClassManagerFrame;
import com.trungtamdaotao.view.academic.CourseManagerFrame;
import com.trungtamdaotao.view.academic.ScheduleManagerFrame;
import com.trungtamdaotao.view.finance.FinanceReportFrame;
import com.trungtamdaotao.view.finance.InvoiceManagerFrame;
import com.trungtamdaotao.view.student.StudentManagerFrame;
import com.trungtamdaotao.view.system.StaffManagerFrame;
import com.trungtamdaotao.view.teacher.TeacherManagerFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainMenuFrame extends JFrame {

    public MainMenuFrame() {
        setTitle("MIS English Center - Dashboard");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIHelper.BACKGROUND_COLOR);

        // --- Header (NORTH) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(UIHelper.PRIMARY_COLOR);
        pnlHeader.setPreferredSize(new Dimension(0, 80));
        
        String welcomeMsg = "HỆ THỐNG QUẢN LÝ TRUNG TÂM NGOẠI NGỮ - Xin chào, " + 
                           (UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getUsername() : "Guest");
        JLabel lblTitle = new JLabel(welcomeMsg);
        lblTitle.setFont(UIHelper.TITLE_FONT);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        pnlHeader.add(lblTitle, BorderLayout.CENTER);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- Dashboard Menu (CENTER) ---
        JPanel pnlDashboard = new JPanel(new GridLayout(0, 3, 20, 20)); // Dynamic rows
        pnlDashboard.setBackground(UIHelper.BACKGROUND_COLOR);
        pnlDashboard.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Phân quyền hiển thị các nút
        addAuthorizedButtons(pnlDashboard);

        add(new JScrollPane(pnlDashboard), BorderLayout.CENTER);

        // --- Footer (SOUTH) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlFooter.setBackground(UIHelper.PRIMARY_COLOR);
        
        JLabel lblRole = new JLabel("Vai trò: " + getRoleDisplayName());
        lblRole.setForeground(Color.WHITE);
        lblRole.setFont(UIHelper.BOLD_FONT);
        pnlFooter.add(lblRole);
        pnlFooter.add(Box.createHorizontalStrut(20));

        JButton btnLogout = UIHelper.createStandardButton("Đăng xuất", UIHelper.WARNING_COLOR, "↩");
        btnLogout.addActionListener(e -> logout());
        pnlFooter.add(btnLogout);

        JButton btnExit = UIHelper.createStandardButton("Thoát", UIHelper.DANGER_COLOR, "🚪");
        btnExit.addActionListener(e -> exitApplication());
        pnlFooter.add(btnExit);
        
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void addAuthorizedButtons(JPanel pnl) {
        IPermission p = UserSession.getPermissions();

        // Module Học thuật
        if (p.canManageAcademic()) {
            pnl.add(createMenuButton("Học viên", "👥", e -> new StudentManagerFrame().setVisible(true)));
            pnl.add(createMenuButton("Lớp học", "🏫", e -> new ClassManagerFrame().setVisible(true)));
            pnl.add(createMenuButton("Khóa học", "📚", e -> new CourseManagerFrame().setVisible(true)));
            pnl.add(createMenuButton("Lịch học", "📅", e -> new ScheduleManagerFrame().setVisible(true)));
            pnl.add(createMenuButton("Điểm danh", "📝", e -> new AttendanceManagerFrame().setVisible(true)));
        }

        // Module Giáo viên & Nhân sự
        if (p.canManageStaff()) {
            pnl.add(createMenuButton("Giáo viên", "👨‍🏫", e -> new TeacherManagerFrame().setVisible(true)));
            pnl.add(createMenuButton("Nhân sự", "👮", e -> new StaffManagerFrame().setVisible(true)));
        }

        // Module Tài chính
        if (p.canManageFinancials()) {
            pnl.add(createMenuButton("Tài chính", "💰", e -> new InvoiceManagerFrame().setVisible(true)));
            pnl.add(createMenuButton("Báo cáo", "📊", e -> new FinanceReportFrame().setVisible(true)));
        }
    }

    private String getRoleDisplayName() {
        if (UserSession.isAdmin()) return "Administrator";
        if (UserSession.isStaff()) return "Staff (" + UserSession.getStaffRole() + ")";
        if (UserSession.isTeacher()) return "Teacher";
        if (UserSession.isStudent()) return "Student";
        return "Unknown";
    }

    private JButton createMenuButton(String text, String icon, java.awt.event.ActionListener listener) {
        JButton btn = new JButton("<html><center><font size='10'>" + icon + "</font><br><br><font size='5'>" + text + "</font></center></html>");
        btn.setFont(UIHelper.BOLD_FONT);
        btn.setBackground(Color.WHITE);
        btn.setForeground(UIHelper.PRIMARY_COLOR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(UIHelper.PRIMARY_COLOR, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(236, 240, 241)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    private void logout() {
        UserSession.logout();
        new com.trungtamdaotao.view.system.LoginFrame().setVisible(true);
        this.dispose();
    }

    private void exitApplication() {
        if (JOptionPane.showConfirmDialog(this, "Thoát ứng dụng?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
