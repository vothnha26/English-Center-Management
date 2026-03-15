package com.trungtamdaotao.model.service.academic;

import com.trungtamdaotao.model.dao.academic.IAttendanceDAO;
import com.trungtamdaotao.model.entity.academic.Attendance;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceService {
    private final IAttendanceDAO attendanceDAO;

    public AttendanceService(IAttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    /**
     * Tạo mới điểm danh với validation
     */
    public void createAttendance(Attendance attendance) {
        // Validation 1: Học viên không được null
        if (attendance.getStudent() == null) {
            throw new IllegalArgumentException("Phải chọn học viên!");
        }

        // Validation 2: Lớp học không được null
        if (attendance.getClazz() == null) {
            throw new IllegalArgumentException("Phải chọn lớp học!");
        }

        // Validation 3: Ngày điểm danh không được null
        if (attendance.getAttend_date() == null) {
            throw new IllegalArgumentException("Ngày điểm danh không được để trống!");
        }

        // Validation 4: Trạng thái không được null
        if (attendance.getStatus() == null) {
            throw new IllegalArgumentException("Phải chọn trạng thái điểm danh!");
        }

        // Validation 5: Kiểm tra trùng điểm danh (1 học viên, 1 lớp, 1 ngày chỉ 1 bản ghi)
        Attendance existing = attendanceDAO.findByStudentAndClassAndDate(
                attendance.getStudent(), attendance.getClazz(), attendance.getAttend_date());
        
        if (existing != null) {
            throw new IllegalArgumentException(
                    "Học viên " + attendance.getStudent().getFullName() + 
                    " đã được điểm danh cho lớp " + attendance.getClazz().getClassName() +
                    " vào ngày " + attendance.getAttend_date() + "!");
        }

        attendanceDAO.save(attendance);
    }

    /**
     * Cập nhật điểm danh
     */
    public void updateAttendance(Attendance attendance) {
        // Validation tương tự create
        if (attendance.getStudent() == null) {
            throw new IllegalArgumentException("Phải chọn học viên!");
        }

        if (attendance.getClazz() == null) {
            throw new IllegalArgumentException("Phải chọn lớp học!");
        }

        if (attendance.getAttend_date() == null) {
            throw new IllegalArgumentException("Ngày điểm danh không được để trống!");
        }

        if (attendance.getStatus() == null) {
            throw new IllegalArgumentException("Phải chọn trạng thái điểm danh!");
        }

        // Kiểm tra trùng điểm danh (ngoại trừ chính nó)
        Attendance existing = attendanceDAO.findByStudentAndClassAndDate(
                attendance.getStudent(), attendance.getClazz(), attendance.getAttend_date());
        
        if (existing != null && !existing.getAttendance_id().equals(attendance.getAttendance_id())) {
            throw new IllegalArgumentException(
                    "Học viên " + attendance.getStudent().getFullName() + 
                    " đã được điểm danh cho lớp " + attendance.getClazz().getClassName() +
                    " vào ngày " + attendance.getAttend_date() + "!");
        }

        attendanceDAO.update(attendance);
    }

    /**
     * Xóa điểm danh
     */
    public void deleteAttendance(int id) {
        attendanceDAO.delete((long) id);
    }

    /**
     * Lấy tất cả điểm danh
     */
    public List<Attendance> getAllAttendances() {
        return attendanceDAO.findAll();
    }

    /**
     * Lấy điểm danh theo ID
     */
    public Attendance getAttendanceById(int id) {
        return attendanceDAO.findByIdWithRelations(id);
    }

    /**
     * Lambda 1: Lọc điểm danh theo học viên
     */
    public List<Attendance> getAttendancesByStudent(Student student) {
        if (student == null) {
            return attendanceDAO.findAll();
        }
        return attendanceDAO.findByStudent(student);
    }

    /**
     * Lambda 2: Lọc điểm danh theo học viên ID
     */
    public List<Attendance> getAttendancesByStudentId(Long studentId) {
        if (studentId == null) {
            return attendanceDAO.findAll();
        }
        return attendanceDAO.findByStudentId(studentId);
    }

    /**
     * Lambda 3: Lọc điểm danh theo lớp học
     */
    public List<Attendance> getAttendancesByClass(ClassEntity clazz) {
        if (clazz == null) {
            return attendanceDAO.findAll();
        }
        return attendanceDAO.findByClass(clazz);
    }

    /**
     * Lambda 4: Lọc điểm danh theo lớp học ID
     */
    public List<Attendance> getAttendancesByClassId(Long classId) {
        if (classId == null) {
            return attendanceDAO.findAll();
        }
        return attendanceDAO.findByClassId(classId);
    }

    /**
     * Lambda 5: Lọc điểm danh theo ngày
     */
    public List<Attendance> getAttendancesByDate(LocalDate date) {
        if (date == null) {
            return attendanceDAO.findAll();
        }
        return attendanceDAO.findByDate(date);
    }

    /**
     * Lambda 6: Lọc điểm danh theo lớp và ngày
     */
    public List<Attendance> getAttendancesByClassAndDate(ClassEntity clazz, LocalDate date) {
        if (clazz == null || date == null) {
            return attendanceDAO.findAll();
        }
        return attendanceDAO.findByClassAndDate(clazz, date);
    }

    /**
     * Lambda 7: Lọc điểm danh theo trạng thái
     */
    public List<Attendance> getAttendancesByStatus(AttendanceStatus status) {
        if (status == null) {
            return attendanceDAO.findAll();
        }
        return attendanceDAO.findByStatus(status);
    }

    /**
     * Lambda 8: Thống kê tỷ lệ có mặt
     */
    public double getAttendanceRate(ClassEntity clazz, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceDAO.findAll().stream()
                .filter(a -> a.getClazz() != null && a.getClazz().equals(clazz))
                .filter(a -> a.getAttend_date() != null && 
                        !a.getAttend_date().isBefore(startDate) && 
                        !a.getAttend_date().isAfter(endDate))
                .collect(Collectors.toList());
        
        if (attendances.isEmpty()) {
            return 0.0;
        }
        
        long presentCount = attendances.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.Present)
                .count();
        
        return (double) presentCount / attendances.size() * 100;
    }

    /**
     * Lambda 9: Đếm số buổi vắng của học viên trong lớp
     */
    public long countAbsentByStudentAndClass(Student student, ClassEntity clazz) {
        return attendanceDAO.findByStudentAndClass(student, clazz).stream()
                .filter(a -> a.getStatus() == AttendanceStatus.Absent)
                .count();
    }
}
