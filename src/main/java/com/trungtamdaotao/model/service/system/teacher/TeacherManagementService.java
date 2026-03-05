package com.trungtamdaotao.model.service.system.teacher;

import com.trungtamdaotao.model.dao.system.ITeacherDAO;
import com.trungtamdaotao.model.dao.system.IUserAccountDAO;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.model.entity.system.UserAccount;

public class TeacherManagementService {
    private final ITeacherDAO teacherDAO;
    private final IUserAccountDAO userAccountDAO;

    public TeacherManagementService(ITeacherDAO teacherDAO, IUserAccountDAO userAccountDAO) {
        this.teacherDAO = teacherDAO;
        this.userAccountDAO = userAccountDAO;
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

    // Hàm 2: Tạo tài khoản độc lập (RBAC)
    public void createAccountForTeacher(Teacher teacher) {
        UserAccount account = new UserAccount();
        account.setUsername(teacher.getEmail());
        account.setPassword_hash("123456"); // Mật khẩu mặc định
        account.setRole(AccountRole.Teacher);
        account.setIs_active(true);

        userAccountDAO.save(account);
    }
}