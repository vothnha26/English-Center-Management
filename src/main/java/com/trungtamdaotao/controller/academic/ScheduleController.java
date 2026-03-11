package com.trungtamdaotao.controller.academic;

import com.trungtamdaotao.model.dao.academic.IScheduleDAO;
import com.trungtamdaotao.model.dao.academic.impl.ScheduleDAOImpl;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Schedule;
import com.trungtamdaotao.model.entity.operations.Room;
import com.trungtamdaotao.model.service.academic.ScheduleService;

import java.time.LocalDate;
import java.util.List;

public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController() {
        IScheduleDAO scheduleDAO = new ScheduleDAOImpl();
        this.scheduleService = new ScheduleService(scheduleDAO);
    }

    /**
     * Tạo lịch học mới
     * @return message thông báo
     */
    public String createSchedule(Schedule schedule) {
        try {
            scheduleService.createSchedule(schedule);
            return "Tạo lịch học thành công!";
        } catch (IllegalArgumentException e) {
            return "Lỗi: " + e.getMessage();
        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    /**
     * Cập nhật lịch học
     * @return message thông báo
     */
    public String updateSchedule(Schedule schedule) {
        try {
            scheduleService.updateSchedule(schedule);
            return "Cập nhật lịch học thành công!";
        } catch (IllegalArgumentException e) {
            return "Lỗi: " + e.getMessage();
        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    /**
     * Xóa lịch học
     * @return message thông báo
     */
    public String deleteSchedule(int id) {
        try {
            scheduleService.deleteSchedule(id);
            return "Xóa lịch học thành công!";
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }

    /**
     * Lấy tất cả lịch học
     */
    public List<Schedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    /**
     * Lấy lịch học theo ID
     */
    public Schedule getScheduleById(int id) {
        return scheduleService.getScheduleById(id);
    }

    /**
     * Lọc lịch học theo lớp
     */
    public List<Schedule> getSchedulesByClass(ClassEntity clazz) {
        return scheduleService.getSchedulesByClass(clazz);
    }

    /**
     * Lọc lịch học theo classId
     */
    public List<Schedule> getSchedulesByClassId(Long classId) {
        return scheduleService.getSchedulesByClassId(classId);
    }

    /**
     * Lọc lịch học theo ngày
     */
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        return scheduleService.getSchedulesByDate(date);
    }

    /**
     * Lọc lịch học theo phòng
     */
    public List<Schedule> getSchedulesByRoom(Room room) {
        return scheduleService.getSchedulesByRoom(room);
    }

    /**
     * Lọc lịch học trong khoảng thời gian
     */
    public List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return scheduleService.getSchedulesByDateRange(startDate, endDate);
    }
}
