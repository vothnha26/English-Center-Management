package com.trungtamdaotao.view.system;

import com.trungtamdaotao.controller.system.PersonnelController;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.entity.system.Staff;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StaffManagerFrame extends JFrame {

    private final PersonnelController controller;
    private JTable tblStaff;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnSearch, btnFilterAdmin, btnReload;

    public StaffManagerFrame() {
        this.controller = new PersonnelController();
        initComponent();
        loadTableData();

        this.setTitle("Hệ Thống Quản Lý Nhân sự - MIS English Center");
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private void initComponent() {
        // 1. Toolbar phía trên
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlHeader.setBorder(BorderFactory.createTitledBorder("Công cụ quản trị"));

        txtSearch = new JTextField(20);
        btnSearch = new JButton("Tìm kiếm");
        btnFilterAdmin = new JButton("Lọc Quản trị viên");
        btnReload = new JButton("Làm mới bảng");

        pnlHeader.add(new JLabel("Tìm tên/email:"));
        pnlHeader.add(txtSearch);
        pnlHeader.add(btnSearch);
        pnlHeader.add(btnFilterAdmin);
        pnlHeader.add(btnReload);

        // 2. Bảng hiển thị
        String[] columns = {"ID", "Họ Tên", "Vai Trò", "Email", "Số điện thoại", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp trên bảng
            }
        };
        tblStaff = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblStaff);

        // 3. Xử lý sự kiện bằng Lambda
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            renderTable(controller.searchStaff(keyword)); // Gọi hàm search từ controller
        });

        btnFilterAdmin.addActionListener(e -> {
            // Sử dụng logic lọc Admin trực tiếp tại View (hoặc gọi qua controller)
            renderTable(controller.getStaffByRole(StaffRole.Manager));
        });

        btnReload.addActionListener(e -> loadTableData());

        this.setLayout(new BorderLayout());
        this.add(pnlHeader, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTableData() {
        renderTable(controller.getActiveStaffList());
    }

    /**
     * PHẦN QUAN TRỌNG: Sửa lỗi chuyển đổi Enum tại đây
     */
    private void renderTable(List<Staff> dataList) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ

        if (dataList != null) {
            dataList.forEach(s -> {
                // Chuyển đổi Enum thành String để JTable hiển thị được
                String roleStr = (s.getRole() != null) ? s.getRole().name() : "N/A";
                String statusStr = (s.getStatus() != null) ? s.getStatus().name() : "N/A";

                tableModel.addRow(new Object[]{
                        s.getStaff_id(),
                        s.getFullName(),
                        roleStr,   // Đã sửa: Truyền String thay vì Enum
                        s.getEmail(),
                        s.getPhone(),
                        statusStr  // Đã sửa: Truyền String thay vì Enum
                });
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffManagerFrame().setVisible(true));
    }
}