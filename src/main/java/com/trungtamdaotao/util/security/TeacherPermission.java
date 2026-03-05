package com.trungtamdaotao.util.security;

// Quyền của Giáo viên
public class TeacherPermission implements UserPermission {
    public boolean canAccessStaffManager() { return false; }
    public boolean canEditSalary() { return false; }
    public boolean canViewSchedule() { return true; }
    public boolean canInputGrades() { return true; }
}
