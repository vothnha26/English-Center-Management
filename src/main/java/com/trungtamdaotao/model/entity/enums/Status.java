package com.trungtamdaotao.model.entity.enums;

public enum Status {
    Active("Đang làm việc"),
    Inactive("Đã nghỉ");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
