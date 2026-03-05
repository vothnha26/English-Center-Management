package com.trungtamdaotao.util.security;

public class GuestPermission implements UserPermission{
    public boolean canAccessStaffManager() { return false; }
    public boolean canEditSalary() { return false; }
    public boolean canViewSchedule() { return false; }
    public boolean canInputGrades() { return false; }
}
