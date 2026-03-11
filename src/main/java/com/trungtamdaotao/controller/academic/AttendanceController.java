package com.trungtamdaotao.controller.academic;

import com.trungtamdaotao.model.dao.academic.IAttendanceDAO;
import com.trungtamdaotao.model.dao.academic.impl.AttendanceDAOImpl;
import com.trungtamdaotao.model.entity.academic.Attendance;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.AttendanceStatus;
import com.trungtamdaotao.model.service.academic.AttendanceService;

import java.time.LocalDate;
import java.util.List;

public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController() {
        IAttendanceDAO attendanceDAO = new AttendanceDAOImpl();
        this.attendanceService = new AttendanceService(attendanceDAO);
    }

    /**
     * Tạo điểm danh mới
     * @return message thông báo
     */
    public String createAttendance(Attendance attendance) {
        try {
            attendanceService.createAttendance(attendance);
            return "Tạo điểm danh thành công!";
        } catch (IllegalArgumentException e) {
            return "Lỗi: " + e.getMessage();
        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    /**
     * Cập nhật điểm danh
     * @return message thông báo
     */
    public String updateAttendance(Attendance attendance) {
        try {
            attendanceService.updateAttendance(attendance);
            return "Cập nhật điểm danh thành công!";
        } catch (IllegalArgumentException e) {
            return "Lỗi: " + e.getMessage();
        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    /**
     * Xóa điểm danh
     * @return message thông báo
     */
    public String deleteAttendance(int id) {
        try {
            attendanceService.deleteAttendance(id);
            return "Xóa điểm danh thành công!";
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }

    /**
     * Lấy tất cả điểm danh
     */
    public List<Attendance> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }

    /**
     * Lấy điểm danh theo ID
     */
    public Attendance getAttendanceById(int id) {
        return attendanceService.getAttendanceById(id);
    }

    /**
     * Lọc điểm danh theo học viên
     */
    public List<Attendance> getAttendancesByStudent(Student student) {
        return attendanceService.getAttendancesByStudent(student);
    }

    /**
     * Lọc điểm danh theo học viên ID
     */
    public List<Attendance> getAttendancesByStudentId(Long studentId) {
        return attendanceService.getAttendancesByStudentId(studentId);
    }

    /**
     * Lọc điểm danh theo lớp học
     */
    public List<Attendance> getAttendancesByClass(ClassEntity clazz) {
        return attendanceService.getAttendancesByClass(clazz);
    }

    /**
     * Lọc điểm danh theo lớp học ID
     */
    public List<Attendance> getAttendancesByClassId(Long classId) {
        return attendanceService.getAttendancesByClassId(classId);
    }

    /**
     * Lọc điểm danh theo ngày
     */
    public List<Attendance> getAttendancesByDate(LocalDate date) {
        return attendanceService.getAttendancesByDate(date);
    }

    /**
     * Lọc điểm danh theo lớp và ngày
     */
    public List<Attendance> getAttendancesByClassAndDate(ClassEntity clazz, LocalDate date) {
        return attendanceService.getAttendancesByClassAndDate(clazz, date);
    }

    /**
     * Lọc điểm danh theo trạng thái
     */
    public List<Attendance> getAttendancesByStatus(AttendanceStatus status) {
        return attendanceService.getAttendancesByStatus(status);
    }

    /**
     * Thống kê tỷ lệ có mặt
     */
    public double getAttendanceRate(ClassEntity clazz, LocalDate startDate, LocalDate endDate) {
        return attendanceService.getAttendanceRate(clazz, startDate, endDate);
    }

    /**
     * Đếm số buổi vắng
     */
    public long countAbsentByStudentAndClass(Student student, ClassEntity clazz) {
        return attendanceService.countAbsentByStudentAndClass(student, clazz);
    }
}
