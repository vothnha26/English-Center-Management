package com.trungtamdaotao.controller.system;

import java.util.List;

import com.trungtamdaotao.model.dao.impl.StaffDAOImpl;
import com.trungtamdaotao.model.dao.impl.TeacherDAOImpl;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.entity.system.Staff;
import com.trungtamdaotao.model.service.system.StaffService;
import com.trungtamdaotao.model.service.system.TeacherService;


public class PersonnelController {
    private final StaffService staffService;
    private final TeacherService teacherService;

    /** DIP-compliant: nhận Service từ bên ngoài (dễ test, dễ thay thế impl) */
    public PersonnelController(StaffService staffService, TeacherService teacherService) {
        this.staffService   = staffService;
        this.teacherService = teacherService;
    }

    /** Convenience constructor — tự khởi tạo impl mặc định khi không có DI framework */
    public PersonnelController() {
        this(new StaffService(new StaffDAOImpl()),
             new TeacherService(new TeacherDAOImpl()));
    }

    public List<Staff> getActiveStaffList() {
        return staffService.getActiveStaffs();
    }

    public List<Teacher> getIELTSList() {
        return teacherService.getTeachersBySpecialty("IELTS");
    }

    public List<Staff> searchStaff(String keyword) {
        return staffService.searchStaff(keyword);
    }

    public List<Staff> getStaffByRole (StaffRole role) {
        return staffService.getStaffByRole(role);
    }
}
