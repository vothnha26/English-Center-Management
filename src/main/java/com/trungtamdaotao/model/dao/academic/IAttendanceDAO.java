package com.trungtamdaotao.model.dao.academic;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.academic.Attendance;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.core.Student;
import com.trungtamdaotao.model.entity.enums.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;

public interface IAttendanceDAO extends IGenericDAO<Attendance> {
    /**
     * Tìm điểm danh theo học viên
     */
    List<Attendance> findByStudent(Student student);

    /**
     * Tìm điểm danh theo học viên ID
     */
    List<Attendance> findByStudentId(Long studentId);

    /**
     * Tìm điểm danh theo lớp học
     */
    List<Attendance> findByClass(ClassEntity clazz);

    /**
     * Tìm điểm danh theo lớp học ID
     */
    List<Attendance> findByClassId(Long classId);

    /**
     * Tìm điểm danh theo ngày
     */
    List<Attendance> findByDate(LocalDate date);

    /**
     * Tìm điểm danh theo lớp và ngày
     */
    List<Attendance> findByClassAndDate(ClassEntity clazz, LocalDate date);

    /**
     * Tìm điểm danh theo lớp ID và ngày
     */
    List<Attendance> findByClassIdAndDate(Long classId, LocalDate date);

    /**
     * Tìm điểm danh theo học viên và lớp
     */
    List<Attendance> findByStudentAndClass(Student student, ClassEntity clazz);

    /**
     * Tìm điểm danh theo học viên, lớp và ngày (để kiểm tra trùng)
     */
    Attendance findByStudentAndClassAndDate(Student student, ClassEntity clazz, LocalDate date);

    /**
     * Tìm điểm danh theo trạng thái
     */
    List<Attendance> findByStatus(AttendanceStatus status);

    /**
     * Lấy điểm danh theo ID với eager loading các entity liên quan
     */
    Attendance findByIdWithRelations(int id);
}
