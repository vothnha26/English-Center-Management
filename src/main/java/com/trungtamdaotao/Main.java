package com.trungtamdaotao;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.trungtamdaotao.view.finance.FinanceReportFrame;
import com.trungtamdaotao.view.finance.InvoiceManagerFrame;
import com.trungtamdaotao.view.student.StudentManagerFrame;
import com.trungtamdaotao.view.system.StaffManagerFrame;

public class Main {
    public static void main(String[] args) {
        // Đặt Look & Feel hệ thống (Windows/Mac/Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame menu = new JFrame("Trung Tâm Đào Tạo - Menu chính");
            menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            menu.setSize(340, 280);
            menu.setLocationRelativeTo(null);
            menu.setLayout(new GridLayout(4, 1, 8, 8));
            menu.getRootPane().setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

            JButton btnStudents  = new JButton("👤  Quản lý Học viên & Ghi danh");
            JButton btnInvoices  = new JButton("🧾  Quản lý Hóa đơn & Thanh toán");
            JButton btnReport    = new JButton("📊  Báo cáo Tài chính");
            JButton btnStaff     = new JButton("🏢  Quản lý Nhân sự");

            btnStudents.addActionListener(e -> new StudentManagerFrame().setVisible(true));
            btnInvoices.addActionListener(e -> new InvoiceManagerFrame().setVisible(true));
            btnReport.addActionListener(e   -> new FinanceReportFrame().setVisible(true));
            btnStaff.addActionListener(e    -> new StaffManagerFrame().setVisible(true));

            menu.add(btnStudents);
            menu.add(btnInvoices);
            menu.add(btnReport);
            menu.add(btnStaff);
            menu.setVisible(true);
        });
    }
}