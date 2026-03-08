package com.trungtamdaotao.model.service.system;

import java.util.List;
import java.util.stream.Collectors;

import com.trungtamdaotao.model.dao.system.IStaffDAO;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.entity.system.UserAccount;

public class StaffService {

    private final IStaffDAO staffDAO;
    private final RegistrationService registrationService;

    public StaffService(IStaffDAO staffDAO) {
        this.staffDAO = staffDAO;
        this.registrationService = new RegistrationService();
    }

    public StaffService(IStaffDAO staffDAO, RegistrationService registrationService) {
        this.staffDAO = staffDAO;
        this.registrationService = registrationService;
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    public List<Staff> getAllStaff() {
        return staffDAO.findAll();
    }

    /** Chỉ hiển thị staff đang Active */
    public List<Staff> getActiveStaff() {
        return staffDAO.findAll().stream()
                .filter(s -> s.getStatus() == Status.Active)
                .collect(Collectors.toList());
    }

    /** Tìm kiếm theo tên hoặc số điện thoại */
    public List<Staff> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllStaff();
        return staffDAO.findByNameOrPhone(keyword.trim());
    }

    public Staff findById(Long id) {
        return staffDAO.findById(id);
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Thêm staff mới.
     * @throws IllegalArgumentException nếu thiếu họ tên hoặc số điện thoại
     */
    public void addStaff(String fullName, StaffRole role, String phone, String email) throws Exception {
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Họ tên không được để trống.");
        if (phone == null || phone.isBlank())
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        if (role == null)
            throw new IllegalArgumentException("Vai trò không được để trống.");

        Staff s = new Staff();
        s.setFullName(fullName.trim());
        s.setRole(role);
        s.setPhone(phone.trim());
        s.setEmail(email);
        s.setStatus(Status.Active);
        staffDAO.save(s);

        // Tự động tạo account nếu có email
        if (email != null && !email.isBlank()) {
            AccountRole accountRole = role == StaffRole.ADMIN ? AccountRole.ADMIN : AccountRole.STAFF;
            registrationService.registerUser(email, email, accountRole, null, null, s);
        }
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    public void updateStaff(Staff staff) {
        if (staff.getFullName() == null || staff.getFullName().isBlank())
            throw new IllegalArgumentException("Họ tên không được để trống.");
        staffDAO.update(staff);
    }

    // ─── SOFT DELETE (đặt trạng thái Inactive thay vì xóa thật) ───────────────

    public void deactivateStaff(Long id) {
        Staff s = staffDAO.findById(id);
        if (s == null) throw new IllegalArgumentException("Không tìm thấy staff id=" + id);
        s.setStatus(Status.Inactive);
        staffDAO.update(s);
    }

    public void resendVerification(String email) throws Exception {
        registrationService.resendVerification(email);
    }
}