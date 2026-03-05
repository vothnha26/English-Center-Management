package com.trungtamdaotao.model.service.system.teacher;

import com.trungtamdaotao.model.dao.system.ITeacherDAO;
import com.trungtamdaotao.model.entity.core.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TeacherQueryServiceTest {
    private ITeacherDAO mockTeacherDAO;
    private TeacherQueryService queryService;

    @BeforeEach
    void setUp() {
        mockTeacherDAO = mock(ITeacherDAO.class);
        queryService = new TeacherQueryService(mockTeacherDAO);
    }

    @Test
    void testSearchByKeyword() {
        // Giả lập dữ liệu có sẵn trong Database
        Teacher t1 = new Teacher(); t1.setFullName("Nguyễn Văn A"); t1.setEmail("a@gmail.com");
        Teacher t2 = new Teacher(); t2.setFullName("Võ Thanh Nhã"); t2.setEmail("nha@gmail.com");

        when(mockTeacherDAO.findAll()).thenReturn(Arrays.asList(t1, t2));

        // Thực hiện tìm kiếm từ khóa "Nhã"
        List<Teacher> results = queryService.searchByKeyword("Nhã");

        // Kiểm tra kết quả
        assertEquals(1, results.size());
        assertEquals("Võ Thanh Nhã", results.get(0).getFullName());

        System.out.println("✅ Test Tìm kiếm: Từ khóa 'Nhã' trả về đúng dữ liệu: " + results.get(0).getFullName());
    }
}