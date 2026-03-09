package com.trungtamdaotao.view;

import com.trungtamdaotao.view.academic.ClassManagerFrame;
import com.trungtamdaotao.view.academic.CourseManagerFrame;
import com.trungtamdaotao.view.system.StaffManagerFrame;

import javax.swing.*;
import java.awt.*;

public class MainMenuFrame extends JFrame {

    public MainMenuFrame() {
        initComponents();
        
        setTitle("MIS English Center - Hệ thống quản lý trung tâm ngoại ngữ");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Title Panel
        JPanel pnlTitle = new JPanel();
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ TRUNG TÂM NGOẠI NGỮ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        pnlTitle.add(lblTitle);
        add(pnlTitle, BorderLayout.NORTH);
        
        // Menu Panel
        JPanel pnlMenu = new JPanel(new GridLayout(4, 1, 10, 10));
        pnlMenu.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Buttons
        JButton btnCourseManagement = new JButton("Quản lý Khóa học");
        JButton btnClassManagement = new JButton("Quản lý Lớp học");
        JButton btnStaffManagement = new JButton("Quản lý Nhân sự");
        JButton btnExit = new JButton("Thoát");
        
        // Style buttons
        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        btnCourseManagement.setFont(buttonFont);
        btnClassManagement.setFont(buttonFont);
        btnStaffManagement.setFont(buttonFont);
        btnExit.setFont(buttonFont);
        
        pnlMenu.add(btnCourseManagement);
        pnlMenu.add(btnClassManagement);
        pnlMenu.add(btnStaffManagement);
        pnlMenu.add(btnExit);
        
        add(pnlMenu, BorderLayout.CENTER);
        
        // Event handlers
        btnCourseManagement.addActionListener(e -> openCourseManager());
        btnClassManagement.addActionListener(e -> openClassManager());
        btnStaffManagement.addActionListener(e -> openStaffManager());
        btnExit.addActionListener(e -> exitApplication());
    }

    private void openCourseManager() {
        SwingUtilities.invokeLater(() -> new CourseManagerFrame().setVisible(true));
    }

    private void openClassManager() {
        SwingUtilities.invokeLater(() -> new ClassManagerFrame().setVisible(true));
    }

    private void openStaffManager() {
        SwingUtilities.invokeLater(() -> new StaffManagerFrame().setVisible(true));
    }

    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn thoát?", 
                "Xác nhận", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
