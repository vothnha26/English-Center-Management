package com.trungtamdaotao.model.dao.operations;

import com.trungtamdaotao.model.dao.IGenericDAO;
import com.trungtamdaotao.model.entity.operations.Room;

import java.util.List;

public interface IRoomDAO extends IGenericDAO<Room> {
    /**
     * Lấy danh sách phòng đang hoạt động
     */
    List<Room> findActiveRooms();
}
