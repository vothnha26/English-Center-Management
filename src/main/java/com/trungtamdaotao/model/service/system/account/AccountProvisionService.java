package com.trungtamdaotao.model.service.system.account;

import com.trungtamdaotao.model.dao.system.IActivationTokenDAO;
import com.trungtamdaotao.model.dao.system.IUserAccountDAO;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.system.ActivationToken;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.service.common.IMailService;
import com.trungtamdaotao.model.service.common.RealMailServiceImpl;
import com.trungtamdaotao.util.PasswordUtil;
import com.trungtamdaotao.util.TokenGenerator;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class AccountProvisionService {
    private final IUserAccountDAO accountDAO;
    private final IActivationTokenDAO tokenDAO;
    private final IMailService mailService;

    public AccountProvisionService(IUserAccountDAO accountDAO, IActivationTokenDAO tokenDAO, IMailService mailService) {
        this.accountDAO = accountDAO;
        this.tokenDAO = tokenDAO;
        this.mailService = mailService;
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
            // 1. Tạo và lưu UserAccount
            UserAccount account = new UserAccount();
            account.setUsername(email);
            account.setIs_active(false); // Tài khoản mới luôn ở trạng thái chờ kích hoạt

            // Gắn kết với Entity cụ thể (Teacher/Staff/Student) thông qua binder
            binder.accept(account);
            account.setRole(role);
            accountDAO.save(account);

            // 2. Tạo ActivationToken (Hết hạn sau 24h)
            String rawToken = TokenGenerator.generateOTP();
            ActivationToken tokenEntity = new ActivationToken();
            tokenEntity.setUser(account);
            // Hash token trước khi lưu xuống DB để đảm bảo bảo mật
            tokenEntity.setToken(PasswordUtil.hashPassword(rawToken));
            tokenEntity.setExpiryTime(LocalDateTime.now().plusHours(168));

            tokenDAO.save(tokenEntity);

            // 3. Gửi Mail thật cho người dùng
            mailService.sendActivationEmail(email, rawToken);

            return true; // Hoàn tất mọi công đoạn thành công
        } catch (Exception e) {
            // Log lỗi để Nhã dễ debug khi chạy console
            System.err.println("Lỗi cấp tài khoản cho " + email + ": " + e.getMessage());
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

        // 4. Tìm ActivationToken cho user này
        ActivationToken tokenEntity = tokenDAO.findByUserId(account.getUser_id());

        // 5. Kiểm tra token có tồn tại không
        if (tokenEntity == null) {
            onFailure.accept("Không tìm thấy mã kích hoạt cho tài khoản này!");
            return;
        }

        // 6. Kiểm tra token có hết hạn không
        if (tokenEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            onFailure.accept("Mã kích hoạt đã hết hạn! Vui lòng yêu cầu cấp lại.");
            return;
        }

        // 7. Kiểm tra mã kích hoạt (OTP)
        if (PasswordUtil.checkPassword(tempToken, tokenEntity.getToken())) {

            // THÀNH CÔNG: Hash mật khẩu mới và bật trạng thái Active
            account.setPassword_hash(PasswordUtil.hashPassword(newPassword));
            account.setIs_active(true);

            accountDAO.update(account);

            // Xóa token sau khi sử dụng
            tokenDAO.delete(tokenEntity.getId());

            onSuccess.run(); // Chạy callback báo thành công cho UI

        } else {
            onFailure.accept("Mã kích hoạt (OTP) không chính xác!");
        }
    }
}