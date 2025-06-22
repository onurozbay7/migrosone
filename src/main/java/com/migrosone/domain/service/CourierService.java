package com.migrosone.domain.service;

import com.migrosone.domain.model.CourierLocation;

public interface CourierService {
    void processLocation(CourierLocation location);

    double getTotalTravelDistance(String courierId);
}
