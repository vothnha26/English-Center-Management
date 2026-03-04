package com.trungtamdaotao.controller.system;

import com.trungtamdaotao.model.dao.impl.StaffDAOImpl;
import com.trungtamdaotao.model.dao.impl.TeacherDAOImpl;
import com.trungtamdaotao.model.dao.system.IStaffDAO;
import com.trungtamdaotao.model.dao.system.ITeacherDAO;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.StaffRole;
import com.trungtamdaotao.model.entity.system.*;
import com.trungtamdaotao.model.service.system.*;

import java.util.List;


public class PersonnelController {
    private final StaffService staffService;
    private final TeacherService teacherService;

    public PersonnelController() {
        // Khởi tạo các Implementation cụ thể
        IStaffDAO sDAO = new StaffDAOImpl();
        ITeacherDAO tDAO = new TeacherDAOImpl();

        this.staffService = new StaffService(sDAO);
        this.teacherService = new TeacherService(tDAO);
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
