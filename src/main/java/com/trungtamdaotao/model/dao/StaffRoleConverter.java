package com.trungtamdaotao.model.dao;

import com.trungtamdaotao.model.entity.enums.StaffRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StaffRoleConverter implements AttributeConverter<StaffRole, String> {

    @Override
    public String convertToDatabaseColumn(StaffRole role) {
        if (role == null) return null;
        return role.name();
    }

    @Override
    public StaffRole convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return StaffRole.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Fallback sang hàm từ custom logic hoặc mặc định
            return StaffRole.fromString(dbData);
        }
    }
}
