/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.dao;

/**
 *
 * @author VICTUS
 */
import com.mycompany.smart.campus.api.model.Room;
import com.mycompany.smart.campus.api.model.Sensor;
import com.mycompany.smart.campus.api.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockDatabase {
    public static final List<Room> ROOMS = new ArrayList<>();
    public static final List<Sensor> SENSORS = new ArrayList<>();
    // Maps a Sensor ID to its history of readings
    public static final Map<String, List<SensorReading>> READINGS = new HashMap<>();

    static {
        Room library = new Room("LIB-301", "Library Quiet Study", 50);
        ROOMS.add(library);

        Sensor co2Sensor = new Sensor("TEMP-001", "CO2", "ACTIVE", "LIB-301");
        co2Sensor.setCurrentValue(400.5);
        SENSORS.add(co2Sensor);
        
        library.getSensorIds().add(co2Sensor.getId());
        READINGS.put(co2Sensor.getId(), new ArrayList<>());
    }
}
