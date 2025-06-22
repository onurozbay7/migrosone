package com.migrosone.domain.service;

import org.springframework.stereotype.Component;

import static com.migrosone.infrastructure.config.Constants.EARTH_RADIUS;

@Component
public class HaversineCalculator implements DistanceCalculator {



    private double haversine(double thetaInRadians) {
        return Math.pow(Math.sin(thetaInRadians / 2), 2);
    }

    @Override
    public double calculate(double startLat, double startLng, double endLat, double endLng) {
        double deltaLat = Math.toRadians(endLat - startLat);
        double deltaLng = Math.toRadians(endLng - startLng);

        double startLatRad = Math.toRadians(startLat);
        double endLatRad = Math.toRadians(endLat);

        double angularDistance = haversine(deltaLat)
                + Math.cos(startLatRad) * Math.cos(endLatRad) * haversine(deltaLng);

        double centralAngle = 2 * Math.atan2(Math.sqrt(angularDistance), Math.sqrt(1 - angularDistance));

        return EARTH_RADIUS * centralAngle;
    }
}
