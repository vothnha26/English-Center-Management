package com.trungtamdaotao.model.service.system.account;

import com.trungtamdaotao.model.dao.system.IUserAccountDAO;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.util.PasswordUtil;
import com.trungtamdaotao.util.TokenGenerator;

import java.util.function.Consumer;

public class AccountProvisionService {
    private final IUserAccountDAO accountDAO;

    public AccountProvisionService(IUserAccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Hàm Generic cấp tài khoản cho bất kỳ loại nhân sự nào
     *
     * @param email:  Dùng làm username
     * @param role:   Quyền của tài khoản
     * @param binder: Hành động gắn kết (link) với Entity cụ thể
     */
    private boolean provisionAccount(String email, AccountRole role, Consumer<UserAccount> binder) {
        try {
            UserAccount account = new UserAccount();
            account.setUsername(email);

            // 1. Tạo mã kích hoạt tạm thời
            String tempToken = TokenGenerator.generateOTP();

            // 2. Hash mã này và lưu vào cột mật khẩu
            account.setPassword_hash(PasswordUtil.hashPassword(tempToken));

            account.setRole(role);
            account.setIs_active(false); // Quan trọng: Tài khoản chưa kích hoạt

            binder.accept(account);
            accountDAO.save(account);

            // 3. Giả lập gửi Email (In ra console để Nhã copy mã test)
            System.out.println("----------------------------------------------");
            System.out.println("SENDING EMAIL TO: " + email);
            System.out.println("YOUR ACTIVATION CODE IS: " + tempToken);
            System.out.println("----------------------------------------------");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean provisionTeacherAccount(Teacher teacher) {
        return provisionAccount(teacher.getEmail(), AccountRole.Teacher,acc -> acc.setTeacher(teacher));
    }

    public boolean provisionStaffAccount(Staff staff) {
        return provisionAccount(staff.getEmail(), AccountRole.Staff, acc -> acc.setStaff(staff));
    }

    public boolean provisionStudentAccount(Student student) {
        return provisionAccount(student.getEmail(), AccountRole.Student, acc -> acc.setStudent(student));
    }

    public void activateAccount(String username, String tempToken, String newPassword,
                                Runnable onSuccess, Consumer<String> onFailure) {

        // 1. Tìm tài khoản trực tiếp
        UserAccount account = accountDAO.findByUsername(username);

        // 2. Kiểm tra tài khoản có tồn tại không
        if (account == null) {
            onFailure.accept("Tài khoản không tồn tại trên hệ thống!");
            return;
        }

        // 3. Kiểm tra xem tài khoản đã kích hoạt chưa
        if (account.isIs_active()) {
            onFailure.accept("Tài khoản này đã được kích hoạt rồi!");
            return;
        }

        // 4. Kiểm tra mã kích hoạt (OTP) lưu trong password_hash
        if (PasswordUtil.checkPassword(tempToken, account.getPassword_hash())) {

            // THÀNH CÔNG: Hash mật khẩu mới và bật trạng thái Active
            account.setPassword_hash(PasswordUtil.hashPassword(newPassword));
            account.setIs_active(true);

            accountDAO.update(account);
            onSuccess.run(); // Chạy callback báo thành công cho UI

        } else {
            onFailure.accept("Mã kích hoạt (OTP) không chính xác!");
        }
    }
}