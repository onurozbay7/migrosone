package com.migrosone.domain.model;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class CourierState {
    private CourierLocation lastLocation;
    private double totalDistance = 0.0;

    private Map<String, LocalDateTime> lastStoreEntryTimes = new HashMap<>();
}
