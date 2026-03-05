package com.trungtamdaotao.model.service.system.teacher;

import com.trungtamdaotao.model.dao.system.ITeacherDAO;
import com.trungtamdaotao.model.dao.system.IUserAccountDAO;
import com.trungtamdaotao.model.entity.core.Teacher;
import com.trungtamdaotao.model.entity.system.UserAccount;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.model.entity.enums.AccountRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TeacherManagementServiceTest {
    private ITeacherDAO mockTeacherDAO;
    private IUserAccountDAO mockAccountDAO;
    private TeacherManagementService service;

    @BeforeEach
    void setUp() {
        mockTeacherDAO = mock(ITeacherDAO.class);
        mockAccountDAO = mock(IUserAccountDAO.class);
        service = new TeacherManagementService(mockTeacherDAO, mockAccountDAO);
    }

    @Test
    void testCreateTeacherAndAccount() {
        Teacher t = new Teacher();
        t.setTeacher_id((Long) 10L);
        t.setEmail("nha@gmail.com");

        service.createTeacher(t);
        service.createAccountForTeacher(t);

        // Kiểm tra xem có gọi save cho cả 2 không
        verify(mockTeacherDAO, times(1)).save(t);
        verify(mockAccountDAO, times(1)).save(any(UserAccount.class));
        System.out.println("✅ Test Thêm & Cấp TK: Đã gọi DAO lưu Teacher và UserAccount thành công.");
    }

    @Test
    void testUpdateTeacher() {
        Teacher t = new Teacher();
        t.setFullName("Võ Thanh Nhã - Updated");

        service.saveOrUpdate(t);

        verify(mockTeacherDAO, times(1)).save(t);
        System.out.println("✅ Test Cập nhật: Đã gọi lệnh SaveOrUpdate cho: " + t.getFullName());
    }

    @Test
    void testTerminateTeacher() {
        Teacher t = new Teacher();
        t.setTeacher_id((Long) 1L);
        t.setStatus(Status.Active);

        // Giả lập: Khi tìm ID 1 thì trả về đối tượng t
        when(mockTeacherDAO.findById((Long) 1L)).thenReturn(t);

        service.changeStatus((Long) 1L, Status.Inactive);

        // Kiểm tra xem trạng thái đã đổi chưa và có gọi update không
        assertEquals(Status.Inactive, t.getStatus());
        verify(mockTeacherDAO, times(1)).update(t);
        System.out.println("✅ Test Cho nghỉ việc: ID 1 đã chuyển thành " + t.getStatus());
    }
}