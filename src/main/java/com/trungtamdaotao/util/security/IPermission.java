package com.trungtamdaotao.util.security;

/**
 * Giao diện định nghĩa các quyền hạn trong hệ thống.
 * Đảm bảo tính trừu tượng (SOLID).
 */
public interface IPermission {
    boolean canDelete();
    boolean canManageStaff();
    boolean canManageFinancials();
    boolean canManageAcademic();
    boolean canAccessModule(String moduleName);
}
