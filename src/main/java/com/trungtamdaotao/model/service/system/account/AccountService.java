package com.trungtamdaotao.model.service.system.account;

import com.trungtamdaotao.model.dao.system.IAccountDAO;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.entity.system.UserAccount;

public class AccountService {

    private final IAccountDAO accountDAO;

    public AccountService(IAccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public UserAccount createAccount(String username, String passwordHash, AccountRole role,
                                     Teacher teacher, Student student, Staff staff) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username không được để trống.");
        if (role == null)
            throw new IllegalArgumentException("Role không được để trống.");

        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPassword_hash(passwordHash != null ? passwordHash : "");
        account.setRole(role);
        account.setTeacher(teacher);
        account.setStudent(student);
        account.setStaff(staff);
        account.setIs_active(false);

        accountDAO.save(account);
        return account;
    }

    public UserAccount createAccount(UserAccount account) {
        if(account != null) {
            accountDAO.save(account);
            return account;
        }
        return null;
    }

    public UserAccount findByUsername(String username) {
        return accountDAO.findByUsername(username);
    }

    public UserAccount findById(Long id) {
        return accountDAO.findById(id);
    }

    public void updateAccount(UserAccount account) {
        accountDAO.update(account);
    }

    public void changePassword(UserAccount account, String newPassword) throws Exception {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }
        String hashedPassword = hashPassword(newPassword);
        account.setPassword_hash(hashedPassword);
        accountDAO.update(account);
    }

    private String hashPassword(String password) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        return java.util.Base64.getEncoder().encodeToString(hash);
    }
}