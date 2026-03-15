package com.trungtamdaotao.view.system;

import com.trungtamdaotao.controller.student.StudentController;
import com.trungtamdaotao.util.UIHelper;
import com.trungtamdaotao.view.common.BaseManagerPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AdminStudentAccountPanel extends BaseManagerPanel {
    private JTextField txtFullName, txtPhone, txtEmail, txtAddress, txtDob;
    private JPasswordField txtPassword;
    private static final StudentController studentController = new StudentController();

    public AdminStudentAccountPanel() { super(); }

    @Override
    protected void initComponents() {
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lbl = new JLabel("TẠO HỒ SƠ & TÀI KHOẢN HỌC VIÊN");
        lbl.setFont(UIHelper.TITLE_FONT);
        lbl.setForeground(UIHelper.PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));
        header.add(lbl, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new MigLayout("wrap 2, insets 10"));
        txtFullName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtAddress = new JTextField();
        txtDob = new JTextField();

        form.add(new JLabel("Họ tên:")); form.add(txtFullName, "w 400!");
        form.add(new JLabel("Số điện thoại:")); form.add(txtPhone, "w 400!");
        form.add(new JLabel("Email (username):")); form.add(txtEmail, "w 400!");
        form.add(new JLabel("Mật khẩu (tùy chọn cho admin):")); form.add(txtPassword = new JPasswordField(), "w 400!");
        form.add(new JLabel("Địa chỉ:")); form.add(txtAddress, "w 400!");
        form.add(new JLabel("Ngày sinh (YYYY-MM-DD):")); form.add(txtDob, "w 200!");

        JButton btnCreate = UIHelper.createStandardButton("Tạo học viên + tài khoản", UIHelper.SUCCESS_COLOR, "\u2705");
        form.add(btnCreate, "span 2, right");
        add(form, BorderLayout.CENTER);

        btnCreate.addActionListener(e -> doCreate());
    }

    @Override protected void handleEvents() {}
    @Override protected void loadTableData() {}

    private void doCreate() {
        try {
            String fn = txtFullName.getText().trim();
            String ph = txtPhone.getText().trim();
            String em = txtEmail.getText().trim();
            String ad = txtAddress.getText().trim();
            String dobStr = txtDob.getText().trim();
            LocalDate dob = dobStr.isBlank() ? null : LocalDate.parse(dobStr);
            String pwd = new String(txtPassword.getPassword()).trim();
            if (pwd.isEmpty()) {
                studentController.addStudent(fn, ph, em, ad, dob);
                UIHelper.showInfo(this, "Đã tạo hồ sơ học viên và tài khoản (nếu có email). Mật khẩu tạm đã được gửi nếu mail cấu hình đúng.");
            } else {
                studentController.addStudent(fn, ph, em, ad, dob, pwd);
                UIHelper.showInfo(this, "Đã tạo hồ sơ học viên và tài khoản với mật khẩu do admin cung cấp.");
            }
            txtFullName.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtAddress.setText(""); txtDob.setText("");
            txtPassword.setText("");
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }
}
