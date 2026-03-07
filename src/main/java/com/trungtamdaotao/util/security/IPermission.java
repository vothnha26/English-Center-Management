package com.trungtamdaotao.util.security;

public interface IPermission {
    boolean canManageSystem();    // Chỉ Admin
    boolean canManageEnrollment(); // Admin hoặc Staff (Consultant)
    boolean canInputGrades();     // Admin hoặc Teacher
}
