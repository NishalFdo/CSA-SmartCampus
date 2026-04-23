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
import com.mycompany.smart.campus.api.exception.LinkedResourceNotFoundException;
import com.mycompany.smart.campus.api.model.Room;
import com.mycompany.smart.campus.api.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private GenericDAO<Sensor> sensorDAO = new GenericDAO<>(MockDatabase.SENSORS);
    private GenericDAO<Room> roomDAO = new GenericDAO<>(MockDatabase.ROOMS);

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = sensorDAO.getAll();
        if (type != null && !type.isEmpty()) {
            return allSensors.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        return allSensors;
    }

    @POST
    public Response createSensor(Sensor sensor) {
        Room room = roomDAO.getById(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room ID does not exist.");
        }

        Sensor created = sensorDAO.add(sensor);
        room.getSensorIds().add(created.getId());
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
