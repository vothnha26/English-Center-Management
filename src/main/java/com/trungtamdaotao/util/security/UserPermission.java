package com.trungtamdaotao.util.security;

public interface UserPermission {
    boolean canAccessStaffManager();
    boolean canEditSalary();
    boolean canViewSchedule();
    boolean canInputGrades();
}