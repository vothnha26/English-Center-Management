package com.trungtamdaotao.model.dao.operations.impl;

import com.trungtamdaotao.model.dao.AbstractDAO;
import com.trungtamdaotao.model.dao.operations.IRoomDAO;
import com.trungtamdaotao.model.entity.enums.Status;
import com.trungtamdaotao.model.entity.operations.Room;
import com.trungtamdaotao.util.DbManager;
import jakarta.persistence.EntityManager;

import java.util.List;

public class RoomDAOImpl extends AbstractDAO<Room> implements IRoomDAO {
    public RoomDAOImpl() {
        super(Room.class);
    }

    @Override
    public List<Room> findActiveRooms() {
        try (EntityManager em = DbManager.getFactory().createEntityManager()) {
            String jpql = "SELECT r FROM Room r WHERE r.status = :status";
            return em.createQuery(jpql, Room.class)
                    .setParameter("status", Status.Active)
                    .getResultList();
        }
    }
}
