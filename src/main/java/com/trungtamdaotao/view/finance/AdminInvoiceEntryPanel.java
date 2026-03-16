package com.trungtamdaotao.view.finance;

import com.trungtamdaotao.controller.finance.FinanceController;
import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class AdminInvoiceEntryPanel extends BaseManagerPanel {
    private JTextField txtStudentId, txtAmount, txtNote;
    private static final FinanceController financeController = new FinanceController();
    private static final StudentController studentController = new StudentController();

    public AdminInvoiceEntryPanel() { super(); }

    @Override
    protected void initComponents() {
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lbl = new JLabel("TẠO HÓA ĐƠN (NHẬP TỪ ADMIN)");
        lbl.setFont(UIHelper.TITLE_FONT);
        lbl.setForeground(UIHelper.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));
        header.add(lbl, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new MigLayout("wrap 2, insets 10"));
        txtStudentId = new JTextField();
        txtAmount = new JTextField();
        txtNote = new JTextField();
        form.add(new JLabel("Student ID:")); form.add(txtStudentId, "w 300!");
        form.add(new JLabel("Số tiền:")); form.add(txtAmount, "w 300!");
        form.add(new JLabel("Ghi chú:")); form.add(txtNote, "w 300!");

        JButton btnCreate = UIHelper.createStandardButton("Tạo hóa đơn", UIHelper.SUCCESS_COLOR, "\u2705");
        form.add(btnCreate, "span 2, right");
        add(form, BorderLayout.CENTER);

        btnCreate.addActionListener(e -> doCreate());
    }

    @Override protected void handleEvents() {}

    @Override protected void loadTableData() {}

    private void doCreate() {
        try {
            Long sid = Long.parseLong(txtStudentId.getText().trim());
            BigDecimal amount = new BigDecimal(txtAmount.getText().trim());
            Student s = studentController.getStudentById(sid);
            if (s == null) { UIHelper.showError(this, "Không tìm thấy học viên."); return; }
            financeController.createInvoice(s, amount, txtNote.getText().trim());
            UIHelper.showInfo(this, "Đã tạo hóa đơn thành công.");
            txtStudentId.setText(""); txtAmount.setText(""); txtNote.setText("");
        } catch (NumberFormatException ex) {
            UIHelper.showError(this, "ID hoặc số tiền không hợp lệ.");
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }
}
