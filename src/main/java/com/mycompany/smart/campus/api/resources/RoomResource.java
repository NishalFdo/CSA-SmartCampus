/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.resources;

/**
 *
 * @author VICTUS
 */
import com.mycompany.smart.campus.api.dao.GenericDAO;
import com.mycompany.smart.campus.api.dao.MockDatabase;
import com.mycompany.smart.campus.api.exception.RoomNotEmptyException;
import com.mycompany.smart.campus.api.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private GenericDAO<Room> roomDAO = new GenericDAO<>(MockDatabase.ROOMS);

    @GET
    public List<Room> getAllRooms() {
        return roomDAO.getAll();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = roomDAO.getById(roomId);
        if (room == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(room).build();
    }

    @POST
    public Response createRoom(Room room) {
        Room created = roomDAO.add(room);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = roomDAO.getById(roomId);
        if (room == null) return Response.status(Response.Status.NOT_FOUND).build();

        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room: active sensors are still assigned.");
        }

        roomDAO.delete(roomId);
        return Response.noContent().build();
    }
}
