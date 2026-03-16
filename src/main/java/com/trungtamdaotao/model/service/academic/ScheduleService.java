package com.trungtamdaotao.model.service.academic;

import com.trungtamdaotao.model.dao.academic.IScheduleDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Schedule;
import com.trungtamdaotao.model.entity.operations.Room;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleService {
    private final IScheduleDAO scheduleDAO;

    public ScheduleService(IScheduleDAO scheduleDAO) {
        this.scheduleDAO = scheduleDAO;
    }

    /**
     * Tạo mới lịch học với validation
     */
    public void createSchedule(Schedule schedule) {
        // Validation 1: Lớp học không được null
        if (schedule.getClazz() == null) {
            throw new IllegalArgumentException("Phải chọn lớp học cho lịch!");
        }

        // Validation 2: Ngày học không được null hoặc rỗng
        if (schedule.getStudyDate() == null) {
            throw new IllegalArgumentException("Ngày học không được để trống!");
        }

        // Validation 3: Thời gian bắt đầu không được null
        if (schedule.getStartTime() == null) {
            throw new IllegalArgumentException("Thời gian bắt đầu không được để trống!");
        }

        // Validation 4: Thời gian kết thúc không được null
        if (schedule.getEndTime() == null) {
            throw new IllegalArgumentException("Thời gian kết thúc không được để trống!");
        }

        // Validation 5: Thời gian bắt đầu phải trước thời gian kết thúc
        if (!schedule.getStartTime().isBefore(schedule.getEndTime())) {
            throw new IllegalArgumentException("Thời gian bắt đầu phải trước thời gian kết thúc!");
        }

        // Validation 6: Kiểm tra trùng lịch phòng học (nếu có phòng)
        if (schedule.getRoom() != null) {
            List<Schedule> existingSchedules = scheduleDAO.findByRoomAndDate(
                    schedule.getRoom(), schedule.getStudyDate());
            
            for (Schedule existing : existingSchedules) {
                // Kiểm tra overlap thời gian
                if (isTimeOverlap(schedule.getStartTime(), schedule.getEndTime(),
                        existing.getStartTime(), existing.getEndTime())) {
                    throw new IllegalArgumentException(
                            "Phòng " + schedule.getRoom().getRoomName() + 
                            " đã có lịch học vào ngày " + schedule.getStudyDate() +
                            " từ " + existing.getStartTime() + " đến " + existing.getEndTime() + "!");
                }
            }
        }

        scheduleDAO.save(schedule);
    }

    /**
     * Cập nhật lịch học
     */
    public void updateSchedule(Schedule schedule) {
        // Validation tương tự create
        if (schedule.getClazz() == null) {
            throw new IllegalArgumentException("Phải chọn lớp học cho lịch!");
        }

        if (schedule.getStudyDate() == null) {
            throw new IllegalArgumentException("Ngày học không được để trống!");
        }

        if (schedule.getStartTime() == null) {
            throw new IllegalArgumentException("Thời gian bắt đầu không được để trống!");
        }

        if (schedule.getEndTime() == null) {
            throw new IllegalArgumentException("Thời gian kết thúc không được để trống!");
        }

        if (!schedule.getStartTime().isBefore(schedule.getEndTime())) {
            throw new IllegalArgumentException("Thời gian bắt đầu phải trước thời gian kết thúc!");
        }

        // Kiểm tra trùng lịch phòng (ngoại trừ chính nó)
        if (schedule.getRoom() != null) {
            List<Schedule> existingSchedules = scheduleDAO.findByRoomAndDate(
                    schedule.getRoom(), schedule.getStudyDate());
            
            for (Schedule existing : existingSchedules) {
                // Bỏ qua chính nó
                if (existing.getSchedule_id().equals(schedule.getSchedule_id())) {
                    continue;
                }
                
                // Kiểm tra overlap thời gian
                if (isTimeOverlap(schedule.getStartTime(), schedule.getEndTime(),
                        existing.getStartTime(), existing.getEndTime())) {
                    throw new IllegalArgumentException(
                            "Phòng " + schedule.getRoom().getRoomName() + 
                            " đã có lịch học vào ngày " + schedule.getStudyDate() +
                            " từ " + existing.getStartTime() + " đến " + existing.getEndTime() + "!");
                }
            }
        }

        scheduleDAO.update(schedule);
    }

    /**
     * Xóa lịch học
     */
    public void deleteSchedule(int id) {
        scheduleDAO.delete((long) id);
    }

    /**
     * Lấy tất cả lịch học
     */
    public List<Schedule> getAllSchedules() {
        return scheduleDAO.findAll();
    }

    /**
     * Lấy lịch học theo ID
     */
    public Schedule getScheduleById(int id) {
        return scheduleDAO.findById((long) id);
    }

    /**
     * Lambda 1: Lọc lịch học theo lớp
     */
    public List<Schedule> getSchedulesByClass(ClassEntity clazz) {
        if (clazz == null) {
            return scheduleDAO.findAll();
        }
        return scheduleDAO.findAll().stream()
                .filter(s -> s.getClazz() != null && s.getClazz().equals(clazz))
                .collect(Collectors.toList());
    }

    /**
     * Lambda 2: Lọc lịch học theo classId
     */
    public List<Schedule> getSchedulesByClassId(Long classId) {
        if (classId == null) {
            return scheduleDAO.findAll();
        }
        return scheduleDAO.findByClassId(classId);
    }

    /**
     * Lambda 3: Lọc lịch học theo ngày
     */
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        if (date == null) {
            return scheduleDAO.findAll();
        }
        return scheduleDAO.findByDate(date);
    }

    /**
     * Lambda 4: Lọc lịch học theo phòng
     */
    public List<Schedule> getSchedulesByRoom(Room room) {
        if (room == null) {
            return scheduleDAO.findAll();
        }
        return scheduleDAO.findByRoom(room);
    }

    /**
     * Lambda 5: Lọc lịch học trong khoảng thời gian
     */
    public List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return scheduleDAO.findAll();
        }
        return scheduleDAO.findByDateRange(startDate, endDate);
    }

    /**
     * Hàm hỗ trợ kiểm tra overlap thời gian
     */
    private boolean isTimeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        // Hai khoảng thời gian overlap nếu:
        // start1 < end2 AND start2 < end1
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
