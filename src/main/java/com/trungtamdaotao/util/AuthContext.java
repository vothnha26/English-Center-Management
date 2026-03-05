package com.trungtamdaotao.util;

import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.util.security.*;
import java.util.EnumMap;
import java.util.Map;

public class AuthContext {
    private static final Map<AccountRole, UserPermission> permissionsMap = new EnumMap<>(AccountRole.class);

    static {
        // Đăng ký quyền cho từng vai trò ngay từ đầu
        permissionsMap.put(AccountRole.Admin, new AdminPermission());
        permissionsMap.put(AccountRole.Teacher, new TeacherPermission());
        // permissionsMap.put(AccountRole.Staff, new StaffPermission());
    }

    private static AccountRole currentRole;

    public static void setCurrentRole(AccountRole role) { currentRole = role; }

    // Lấy bộ quyền của người dùng hiện tại mà không cần if-else
    public static UserPermission getPermission() {
        return permissionsMap.getOrDefault(currentRole, new GuestPermission());
    }
}
