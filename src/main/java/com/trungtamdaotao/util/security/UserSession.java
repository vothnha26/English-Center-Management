package com.trungtamdaotao.util.security;

import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.entity.system.UserAccount;

/**
 * Quản lý phiên làm việc và cung cấp quyền hạn (SOLID).
 */
public class UserSession {
    private static UserAccount currentUser;
    private static IPermission permissions;

    public static void login(UserAccount user) {
        currentUser = user;
        permissions = new UserPermissionImpl(user);
    }

    public static void logout() {
        currentUser = null;
        permissions = null;
    }

    public static UserAccount getCurrentUser() {
        return currentUser;
    }

    /** Trả về đối tượng quản lý quyền (Interface) */
    public static IPermission getPermissions() {
        if (permissions == null) {
            permissions = new UserPermissionImpl(null); // Trả về quyền trống nếu chưa login
        }
        return permissions;
    }

    // Các helper nhanh để truy cập dữ liệu
    public static Staff getStaffProfile() { return (currentUser != null) ? currentUser.getStaff() : null; }
    public static StaffRole getStaffRole() { Staff s = getStaffProfile(); return (s != null) ? s.getRole() : null; }
    public static boolean isAdmin() { return currentUser != null && currentUser.getRole() == AccountRole.Admin; }
    public static boolean isStaff() { return currentUser != null && currentUser.getRole() == AccountRole.Staff; }
    public static boolean isTeacher() { return currentUser != null && currentUser.getRole() == AccountRole.Teacher; }
    public static boolean isStudent() { return currentUser != null && currentUser.getRole() == AccountRole.Student; }
}
