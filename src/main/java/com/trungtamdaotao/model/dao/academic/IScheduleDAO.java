package com.trungtamdaotao.model.dao.academic;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.academic.ClassEntity;
import com.trungtamdaotao.model.entity.academic.Schedule;
import com.trungtamdaotao.model.entity.operations.Room;

import java.time.LocalDate;
import java.util.List;

public interface IScheduleDAO extends IGenericDAO<Schedule> {
    /**
     * Tìm lịch học theo lớp học
     */
    List<Schedule> findByClass(ClassEntity clazz);

    /**
     * Tìm lịch học theo classId
     */
    List<Schedule> findByClassId(Long classId);

    /**
     * Tìm lịch học theo ngày
     */
    List<Schedule> findByDate(LocalDate date);

    /**
     * Tìm lịch học theo phòng
     */
    List<Schedule> findByRoom(Room room);

    /**
     * Tìm lịch học theo phòng và ngày (để kiểm tra trùng lịch)
     */
    List<Schedule> findByRoomAndDate(Room room, LocalDate date);

    /**
     * Tìm lịch học trong khoảng thời gian
     */
    List<Schedule> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Lấy lịch học theo ID với eager loading các entity liên quan
     */
    Schedule findByIdWithRelations(int id);
}
