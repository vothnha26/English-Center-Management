package com.trungtamdaotao.controller.system;

import com.trungtamdaotao.model.dao.impl.TeacherDAOImpl;
import com.trungtamdaotao.model.dao.impl.UserAccountDAOImpl;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.model.service.system.teacher.TeacherManagementService;
import com.trungtamdaotao.model.service.system.teacher.TeacherQueryService;
import java.util.List;

/**
 * Controller điều phối quản lý nhân sự (Giảng viên & Tài khoản hệ thống)
 * Tuân thủ SOLID: Tách biệt logic Management (Write) và Query (Read)
 */
public class PersonnelController {

    private final TeacherManagementService managementService;
    private final TeacherQueryService queryService;

    public PersonnelController() {
        // Khởi tạo các DAO cần thiết
        TeacherDAOImpl teacherDAO = new TeacherDAOImpl();
        UserAccountDAOImpl userAccountDAO = new UserAccountDAOImpl();

        // Tiêm (Inject) DAO vào các Service tương ứng
        this.managementService = new TeacherManagementService(teacherDAO, userAccountDAO);
        this.queryService = new TeacherQueryService(teacherDAO);
    }

    // =========================================================================
    // PHẦN 1: NGHIỆP VỤ QUẢN LÝ (WRITE OPERATIONS - TeacherManagementService)
    // =========================================================================

    /**
     * Quy trình thêm giảng viên mới và tự động tạo tài khoản hệ thống
     */
    public void addNewTeacherWithAccount(Teacher teacher) {
        try {
            // Bước 1: Tạo thông tin giảng viên
            managementService.createTeacher(teacher);

            // Bước 2: Tự động tạo tài khoản đăng nhập (Username là Email)
            managementService.createAccountForTeacher(teacher);

            System.out.println("✅ Thành công: Đã thêm giảng viên và tạo tài khoản.");
        } catch (Exception e) {
            System.err.println("❌ Lỗi quy trình thêm mới: " + e.getMessage());
        }
    }

    /**
     * Cập nhật thông tin chi tiết giảng viên
     */
    public void updateTeacherInfo(Teacher teacher) {
        managementService.saveOrUpdate(teacher);
    }

    /**
     * Xử lý cho giảng viên nghỉ việc (Cập nhật trạng thái thay vì xóa cứng)
     */
    public void terminateTeacher(Long id) {
        managementService.changeStatus(id, Status.Inactive);
    }

    // =========================================================================
    // PHẦN 2: NGHIỆP VỤ TRA CỨU (READ OPERATIONS - TeacherQueryService)
    // =========================================================================

    /**
     * Lấy toàn bộ danh sách giảng viên
     */
    public List<Teacher> getAllTeachers() {
        return queryService.findAll();
    }

    /**
     * Tìm kiếm đa năng theo Họ tên hoặc Email (Sử dụng Lambda ở Service)
     */
    public List<Teacher> searchTeachers(String keyword) {
        return queryService.searchByKeyword(keyword);
    }

    /**
     * Lọc giảng viên theo chuyên môn (IELTS, TOEIC...)
     */
    public List<Teacher> filterBySpecialty(String specialty) {
        return queryService.filterBySpecialty(specialty);
    }

    /**
     * Lọc danh sách theo trạng thái (Đang làm việc / Đã nghỉ)
     */
    public List<Teacher> filterByStatus(Status status) {
        return queryService.filterByStatus(status);
    }
}