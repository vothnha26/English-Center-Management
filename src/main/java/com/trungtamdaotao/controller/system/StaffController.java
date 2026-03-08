package com.trungtamdaotao.controller.system;

import java.util.List;

import com.trungtamdaotao.model.dao.impl.StaffDAOImpl;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.service.system.StaffService;

public class StaffController {

    private final StaffService staffService;

    /** DIP-compliant: nhận Service từ bên ngoài (dễ test, dễ thay thế impl) */
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    /** Convenience constructor — tự khởi tạo impl mặc định khi không có DI framework */
    public StaffController() {
        this(new StaffService(new StaffDAOImpl()));
    }

    // ─── Staff ──────────────────────────────────────────────────────────────

    public List<Staff> getAllStaff() {
        return staffService.getActiveStaff();
    }

    public List<Staff> searchStaff(String keyword) {
        return staffService.search(keyword);
    }

    public Staff getStaffById(Long id) {
        return staffService.findById(id);
    }

    public void addStaff(String fullName, StaffRole role, String phone, String email) {
        staffService.addStaff(fullName, role, phone, email);
    }

    public void updateStaff(Staff staff) {
        staffService.updateStaff(staff);
    }

    /** Xóa mềm: chuyển trạng thái → Inactive */
    public void deleteStaff(Long id) {
        staffService.deactivateStaff(id);
    }
}