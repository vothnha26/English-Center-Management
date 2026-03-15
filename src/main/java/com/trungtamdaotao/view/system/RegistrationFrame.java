package com.trungtamdaotao.view.system;

import com.trungtamdaotao.model.dao.student.impl.StudentDAOImpl;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.service.system.account.RegistrationService;
import com.trungtamdaotao.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * Simple registration frame for students.
 */
public class RegistrationFrame extends JFrame {

    private JTextField txtFullName, txtPhone, txtEmail, txtAddress, txtDob;
    private JButton btnRegister, btnCancel;

    public RegistrationFrame() {
        setTitle("Đăng ký tài khoản - Học viên");
        setSize(420, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(6, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        p.add(new JLabel("Họ và tên:")); txtFullName = new JTextField(); p.add(txtFullName);
        p.add(new JLabel("Số điện thoại:")); txtPhone = new JTextField(); p.add(txtPhone);
        p.add(new JLabel("Email (đồng thời là username):")); txtEmail = new JTextField(); p.add(txtEmail);
        p.add(new JLabel("Địa chỉ:")); txtAddress = new JTextField(); p.add(txtAddress);
        p.add(new JLabel("Ngày sinh (yyyy-MM-dd):")); txtDob = new JTextField(); p.add(txtDob);

        btnRegister = UIHelper.createStandardButton("Đăng ký", UIHelper.PRIMARY_COLOR, "");
        btnCancel = UIHelper.createStandardButton("Huỷ", Color.GRAY, "");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(btnCancel); actions.add(btnRegister);

        add(p, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());

        btnRegister.addActionListener(e -> doRegister());
    }

    private void doRegister() {
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();
        String dobStr = txtDob.getText().trim();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên, số điện thoại và email là bắt buộc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Create student entity first and save it so we can bind it to account
            Student s = new Student();
            s.setFullName(fullName);
            s.setPhone(phone);
            s.setEmail(email);
            s.setAddress(address);
            s.setRegistrationDate(LocalDate.now());
            if (!dobStr.isBlank()) s.setDateOfBirth(LocalDate.parse(dobStr));

            // persist student
            new StudentDAOImpl().save(s);

            // Register account with role STUDENT and bind created student
            RegistrationService regService = new RegistrationService();
            regService.registerUser(email, email, AccountRole.Student, acct -> acct.setStudent(s));

            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Kiểm tra email để nhận mật khẩu tạm và hướng dẫn kích hoạt.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
