package com.trungtamdaotao.view.academic;

import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.DashboardChart;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * Dashboard dành riêng cho Giáo viên - Đã bổ sung thống kê hiệu suất Pass/Fail.
 */
public class TeacherDashboardPanel extends JPanel {

    public TeacherDashboardPanel() {
        setLayout(new MigLayout("wrap 3, fillx, insets 20", "[fill, grow]", "[]25[]25[grow]"));
        setBackground(UIHelper.BACKGROUND_COLOR);
        initComponents();
    }

    private void initComponents() {
        // --- KPI Row ---
        add(UIHelper.createDashboardCard("LỊCH DẠY HÔM NAY", "3 lớp học", UIHelper.PRIMARY_COLOR), "grow");
        add(UIHelper.createDashboardCard("LỚP CHƯA NHẬP ĐIỂM", "2 lớp", UIHelper.SECONDARY_COLOR), "grow");
        add(UIHelper.createDashboardCard("TỶ LỆ ĐẠT (PASS)", "92%", UIHelper.SUCCESS_COLOR), "grow");

        // --- QUICK ACTIONS ---
        JPanel pnlQuick = new JPanel(new MigLayout("insets 0, fillx", "[grow][grow][grow]"));
        pnlQuick.setOpaque(false);
        pnlQuick.add(UIHelper.createStandardButton("ĐIỂM DANH NHANH", UIHelper.SUCCESS_COLOR, "\u2705"), "grow, height 50");
        pnlQuick.add(UIHelper.createStandardButton("NHẬP ĐIỂM NGAY", UIHelper.PRIMARY_COLOR, "\u270E"), "grow, height 50");
        pnlQuick.add(UIHelper.createStandardButton("XEM LỚP ĐÃ KẾT THÚC", UIHelper.ACCENT_COLOR, "\uD83D\uDCC1"), "grow, height 50");
        
        add(new JLabel("THAO TÁC NHANH"), "span 3, gapy 10 0");
        add(pnlQuick, "span 3, growx");

        // --- CHARTS ---
        JPanel pnlChart1 = new JPanel(new BorderLayout());
        pnlChart1.setBackground(Color.WHITE);
        pnlChart1.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        pnlChart1.add(new DashboardChart("TỶ LỆ CHUYÊN CẦN", Arrays.asList(80, 85, 90, 88, 92, 95), UIHelper.SUCCESS_COLOR), BorderLayout.CENTER);
        
        JPanel pnlChart2 = new JPanel(new BorderLayout());
        pnlChart2.setBackground(Color.WHITE);
        pnlChart2.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        pnlChart2.add(new DashboardChart("BIẾN ĐỘNG ĐIỂM TRUNG BÌNH", Arrays.asList(6, 7, 8, 7, 9, 8), UIHelper.PRIMARY_COLOR), BorderLayout.CENTER);

        add(pnlChart1, "span 2, grow, height 350");
        add(pnlChart2, "grow, height 350");
    }
}
