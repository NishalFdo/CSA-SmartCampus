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
import com.mycompany.smart.campus.api.exception.SensorUnavailableException;
import com.mycompany.smart.campus.api.model.Sensor;
import com.mycompany.smart.campus.api.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    
    private String sensorId;
    private GenericDAO<Sensor> sensorDAO = new GenericDAO<>(MockDatabase.SENSORS);

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getReadings() {
        return MockDatabase.READINGS.getOrDefault(sensorId, new ArrayList<>());
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor parentSensor = sensorDAO.getById(sensorId);
        
        if (parentSensor == null) {
            throw new WebApplicationException("Sensor not found", 404);
        }

        if ("MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus()) || "OFFLINE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is disconnected or in maintenance.");
        }

        if(reading.getId() == null) reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());
        
        MockDatabase.READINGS.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);

        parentSensor.setCurrentValue(reading.getValue());
        sensorDAO.update(parentSensor);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
