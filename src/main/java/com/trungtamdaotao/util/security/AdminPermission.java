package com.trungtamdaotao.util.security;

// Quyền của Manager
public class AdminPermission implements UserPermission {
    public boolean canAccessStaffManager() { return true; }
    public boolean canEditSalary() { return true; }
    public boolean canViewSchedule() { return true; }
    public boolean canInputGrades() { return true; }
}
