package com.trungtamdaotao.view;

import com.trungtamdaotao.util.UIHelper;
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
        setTitle("MIS English Center - Hệ thống quản lý trung tâm ngoại ngữ");
        setSize(1000, 700);
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
        
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ TRUNG TÂM NGOẠI NGỮ");
        lblTitle.setFont(UIHelper.TITLE_FONT);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        pnlHeader.add(lblTitle, BorderLayout.CENTER);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- Dashboard Menu (CENTER) ---
        JPanel pnlDashboard = new JPanel(new GridLayout(3, 3, 20, 20));
        pnlDashboard.setBackground(UIHelper.BACKGROUND_COLOR);
        pnlDashboard.setBorder(new EmptyBorder(30, 30, 30, 30));

        pnlDashboard.add(createMenuButton("Học viên", "👥", e -> new StudentManagerFrame().setVisible(true)));
        pnlDashboard.add(createMenuButton("Giáo viên", "👨‍🏫", e -> new TeacherManagerFrame().setVisible(true)));
        pnlDashboard.add(createMenuButton("Lớp học", "🏫", e -> new ClassManagerFrame().setVisible(true)));
        
        pnlDashboard.add(createMenuButton("Khóa học", "📚", e -> new CourseManagerFrame().setVisible(true)));
        pnlDashboard.add(createMenuButton("Lịch học", "📅", e -> new ScheduleManagerFrame().setVisible(true)));
        pnlDashboard.add(createMenuButton("Điểm danh", "📝", e -> new AttendanceManagerFrame().setVisible(true)));
        
        pnlDashboard.add(createMenuButton("Tài chính", "💰", e -> new InvoiceManagerFrame().setVisible(true)));
        pnlDashboard.add(createMenuButton("Báo cáo", "📊", e -> new FinanceReportFrame().setVisible(true)));
        pnlDashboard.add(createMenuButton("Nhân sự", "👮", e -> new StaffManagerFrame().setVisible(true)));

        add(pnlDashboard, BorderLayout.CENTER);

        // --- Footer (SOUTH) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setBackground(UIHelper.PRIMARY_COLOR);
        
        JButton btnExit = UIHelper.createStandardButton("Thoát", UIHelper.DANGER_COLOR, "🚪");
        btnExit.addActionListener(e -> exitApplication());
        pnlFooter.add(btnExit);
        
        add(pnlFooter, BorderLayout.SOUTH);
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
        
        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(236, 240, 241));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
        
        return btn;
    }

    private void exitApplication() {
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn thoát?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
