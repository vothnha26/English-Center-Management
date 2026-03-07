package com.trungtamdaotao.util.security;

import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.StaffRole;

public class UserPermissionImpl implements IPermission {
    private final UserAccount user;

    public UserPermissionImpl(UserAccount user) {
        this.user = user;
    }

    @Override
    public boolean canManageSystem() {
        // Chỉ Admin thực thụ mới được quản lý hệ thống
        return user.getRole() == AccountRole.Admin;
    }

    @Override
    public boolean canManageEnrollment() {
        if (canManageSystem()) return true;
        // Kiểm tra nếu là Staff và có vai trò Tư vấn (Consultant)
        return user.getStaff() != null &&
                user.getStaff().getRole() == StaffRole.CONSULTANT;
    }

    @Override
    public boolean canInputGrades() {
        if (canManageSystem()) return true;
        // Kiểm tra nếu là Giảng viên (Teacher)
        return user.getTeacher() != null;
    }
}