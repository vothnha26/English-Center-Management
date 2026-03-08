package com.trungtamdaotao.model.service.system.teacher;

import com.trungtamdaotao.model.dao.system.ITeacherDAO;
import com.trungtamdaotao.model.dao.system.IUserAccountDAO;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.model.service.system.account.AccountProvisionService;

public class TeacherManagementService {
    private final ITeacherDAO teacherDAO;
    private final IUserAccountDAO userAccountDAO;
    private final AccountProvisionService accountProvisionService;

    public TeacherManagementService(ITeacherDAO teacherDAO, IUserAccountDAO userAccountDAO) {
        this.teacherDAO = teacherDAO;
        this.userAccountDAO = userAccountDAO;
        // Note: AccountProvisionService needs to be injected or created with dependencies
        // For now, assuming it's created elsewhere and passed in
        this.accountProvisionService = null; // TODO: Inject properly
    }

    public void saveOrUpdate(Teacher teacher) {
        teacherDAO.save(teacher);
    }

    public void delete(Long id) {
        teacherDAO.delete(id);
    }

    public void changeStatus(Long id, Status newStatus) {
        Teacher teacher = teacherDAO.findById(id);
        if (teacher != null) {
            teacher.setStatus(newStatus);
            teacherDAO.update(teacher);
        }
    }

    // Hàm 1: Lưu thông tin giảng viên
    public void createTeacher(Teacher teacher) {
        teacherDAO.save(teacher);
    }

    // Hàm 2: Tạo tài khoản với activation flow
    public void createAccountForTeacher(Teacher teacher) {
        if (accountProvisionService != null) {
            accountProvisionService.provisionTeacherAccount(teacher);
        } else {
            // Fallback to old method if not available
            // TODO: Remove this fallback once AccountProvisionService is properly injected
            System.err.println("AccountProvisionService not available, using fallback");
            // Old code...
        }
    }
}