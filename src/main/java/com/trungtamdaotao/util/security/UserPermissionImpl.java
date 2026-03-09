package com.trungtamdaotao.util.security;

import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.entity.system.UserAccount;

import java.util.*;

public class UserPermissionImpl implements IPermission {
    private final UserAccount user;
    
    // Bảng đăng ký quyền cho từng StaffRole
    private static final Map<StaffRole, EnumSet<PermissionType>> STAFF_PERMISSIONS = new EnumMap<>(StaffRole.class);

    static {
        // MANAGER: Toàn quyền trong Staff Role
        STAFF_PERMISSIONS.put(StaffRole.MANAGER, EnumSet.allOf(PermissionType.class));
        
        // CONSULTANT: Chỉ học thuật
        STAFF_PERMISSIONS.put(StaffRole.CONSULTANT, EnumSet.of(PermissionType.MANAGE_ACADEMIC));
        
        // ACCOUNTANT: Tài chính + Báo cáo
        STAFF_PERMISSIONS.put(StaffRole.ACCOUNTANT, EnumSet.of(PermissionType.MANAGE_FINANCE, PermissionType.VIEW_REPORTS));
        
        // OTHER: Không có quyền mặc định
        STAFF_PERMISSIONS.put(StaffRole.OTHER, EnumSet.noneOf(PermissionType.class));
    }

    public UserPermissionImpl(UserAccount user) {
        this.user = user;
    }

    /** Kiểm tra xem user hiện tại có sở hữu quyền X không */
    private boolean hasPermission(PermissionType type) {
        if (user == null) return false;
        
        // Lớp 1: Admin luôn có mọi quyền
        if (user.getRole() == AccountRole.ADMIN) return true;

        // Lớp 2: Staff kiểm tra qua bảng Registry
        if (user.getRole() == AccountRole.STAFF && user.getStaff() != null) {
            StaffRole role = user.getStaff().getRole();
            return STAFF_PERMISSIONS.getOrDefault(role, EnumSet.noneOf(PermissionType.class))
                                    .contains(type);
        }

        // Teacher/Student có thể thêm logic riêng tại đây (hoặc mở rộng Registry)
        return false;
    }

    @Override public boolean canDelete() { return hasPermission(PermissionType.DELETE_DATA); }
    @Override public boolean canManageStaff() { return hasPermission(PermissionType.MANAGE_STAFF); }
    @Override public boolean canManageFinancials() { return hasPermission(PermissionType.MANAGE_FINANCE); }
    @Override public boolean canManageAcademic() { return hasPermission(PermissionType.MANAGE_ACADEMIC); }

    @Override
    public boolean canAccessModule(String moduleName) {
        if (user == null) return false;
        if (user.getRole() == AccountRole.ADMIN) return true;
        
        // Ví dụ dùng Lambda để so sánh linh hoạt
        return STAFF_PERMISSIONS.getOrDefault(user.getStaff().getRole(), EnumSet.noneOf(PermissionType.class))
                .stream().anyMatch(p -> p.name().contains(moduleName));
    }
}
