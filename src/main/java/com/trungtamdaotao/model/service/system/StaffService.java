package com.trungtamdaotao.model.service.system;

import com.trungtamdaotao.model.dao.system.IStaffDAO;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import java.util.List;
import java.util.stream.Collectors;

public class StaffService {
    private final IStaffDAO staffDAO;

    public StaffService(IStaffDAO staffDAO) {
        this.staffDAO = staffDAO;
    }

    /**
     * Lambda 1: Lọc danh sách nhân viên đang hoạt động (Active)
     */
    public List<Staff> getActiveStaffs() {
        return staffDAO.findAll().stream()
                .filter(s -> s.getStatus() == Status.Active)
                .collect(Collectors.toList());
    }

    /**
     * Lambda 2: Tìm kiếm nhân viên theo tên hoặc Email (Search đa năng)
     */
    public List<Staff> searchStaff(String keyword) {
        String lowerKey = keyword.toLowerCase();
        return staffDAO.findAll().stream()
                .filter(s -> s.getFullName().toLowerCase().contains(lowerKey)
                        || s.getEmail().toLowerCase().contains(lowerKey))
                .toList();
    }

    /**
     * Lambda 3: Lọc nhân viên theo vai trò (Admin, Accountant, Manager...)
     */
    public List<Staff> getStaffByRole(StaffRole role) {
        return staffDAO.findAll().stream()
                .filter(s -> s.getRole() == role)
                .toList();
    }
}