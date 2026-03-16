package com.trungtamdaotao.model.entity.enums;

public enum StaffRole {
    ADMIN, CONSULTANT, ACCOUNTANT, MANAGER, OTHER;

    public static StaffRole fromString(String value) {
        if (value == null) return OTHER;
        for (StaffRole role : StaffRole.values()) {
            if (role.name().equalsIgnoreCase(value.trim())) {
                return role;
            }
        }
        return OTHER;
    }
}
