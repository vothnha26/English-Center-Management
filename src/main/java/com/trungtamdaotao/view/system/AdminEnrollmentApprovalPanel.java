package com.trungtamdaotao.view.system;

import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.academic.Enrollment;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminEnrollmentApprovalPanel extends BaseManagerPanel {
    private JTable tbl;
    private DefaultTableModel model;
    private StudentController controller;

    private void ensureController() {
        if (controller == null) controller = new StudentController();
    }

    public AdminEnrollmentApprovalPanel() { super(); }

    @Override
    protected void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10,10,20,10));
        JLabel lbl = new JLabel("DANH SÁCH YÊU CẦU GHI DANH");
        lbl.setFont(UIHelper.TITLE_FONT);
        lbl.setForeground(UIHelper.PRIMARY_COLOR);
        header.add(lbl, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        JButton btnApprove = UIHelper.createStandardButton("Phê duyệt", UIHelper.SUCCESS_COLOR, "\u2714");
        JButton btnReject = UIHelper.createStandardButton("Từ chối", UIHelper.DANGER_COLOR, "\u2716");
        btns.add(btnApprove); btns.add(btnReject);
        header.add(btns, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Học viên", "Lớp", "Ngày yêu cầu", "Trạng thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tbl = new JTable(model);
        setupTable(tbl);
        tbl.setRowHeight(36);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        btnApprove.addActionListener(e -> doApprove());
        btnReject.addActionListener(e -> doReject());
    }

    @Override protected void handleEvents() {}

    @Override
    protected void loadTableData() {
        model.setRowCount(0);
        ensureController();
        List<Enrollment> list = controller.getPendingEnrollments();
        for (Enrollment en : list) {
            String className = en.getClazz() != null ? en.getClazz().getClassName() : "-";
            model.addRow(new Object[]{en.getEnrollmentId(), en.getStudent().getFullName(), className, en.getEnrollmentDate(), en.getStatus()});
        }
    }

    private void doApprove() {
        int r = tbl.getSelectedRow();
        if (r < 0) { UIHelper.showError(this, "Vui lòng chọn một yêu cầu."); return; }
        Long id = (Long) model.getValueAt(r, 0);
        try {
            ensureController();
            controller.approveEnrollment(id);
            UIHelper.showInfo(this, "Đã phê duyệt ghi danh.");
            loadTableData();
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void doReject() {
        int r = tbl.getSelectedRow();
        if (r < 0) { UIHelper.showError(this, "Vui lòng chọn một yêu cầu."); return; }
        Long id = (Long) model.getValueAt(r, 0);
        try {
            ensureController();
            controller.cancelEnrollment(id);
            UIHelper.showInfo(this, "Đã từ chối (hủy) yêu cầu.");
            loadTableData();
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }
}
