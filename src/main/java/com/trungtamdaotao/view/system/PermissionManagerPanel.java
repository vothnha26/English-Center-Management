package com.trungtamdaotao.view.system;

import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ma trận phân quyền chuyên nghiệp.
 */
public class PermissionManagerPanel extends BaseManagerPanel {
    private JTable tblMatrix;
    private DefaultTableModel tableModel;

    public PermissionManagerPanel() {
        super();
    }

    @Override
    protected void initComponents() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(10, 10, 20, 10));

        JLabel lblTitle = new JLabel("MA TRẬN PHÂN QUYỀN HỆ THỐNG");
        lblTitle.setFont(UIHelper.TITLE_FONT);
        lblTitle.setForeground(UIHelper.PRIMARY_COLOR);
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        JButton btnSave = UIHelper.createStandardButton("Lưu cấu hình", UIHelper.SUCCESS_COLOR, "\uD83D\uDCBE");
        pnlHeader.add(btnSave, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- PERMISSION MATRIX TABLE ---
        String[] columns = {"Chức năng hệ thống", "Quản trị viên (Admin)", "Giáo viên", "Nhân viên đào tạo", "Nhân viên tài chính"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? String.class : Boolean.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Chỉ cho sửa các ô checkbox
            }
        };

        tblMatrix = new JTable(tableModel);
        setupTable(tblMatrix);
        tblMatrix.setRowHeight(40);
        
        // Dữ liệu mẫu
        tableModel.addRow(new Object[]{"Quản lý Học viên", true, true, true, false});
        tableModel.addRow(new Object[]{"Quản lý Giáo viên", true, false, true, false});
        tableModel.addRow(new Object[]{"Xếp lịch học", true, false, true, false});
        tableModel.addRow(new Object[]{"Điểm danh", true, true, true, false});
        tableModel.addRow(new Object[]{"Tài chính & Hóa đơn", true, false, false, true});
        tableModel.addRow(new Object[]{"Báo cáo doanh thu", true, false, false, true});
        tableModel.addRow(new Object[]{"Cấu hình hệ thống", true, false, false, false});

        add(new JScrollPane(tblMatrix), BorderLayout.CENTER);
    }

    @Override protected void handleEvents() {}
    @Override protected void loadTableData() {}
}
