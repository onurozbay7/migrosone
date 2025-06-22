package com.migrosone.domain.service;

public interface DistanceCalculator {
    double calculate(double lat1, double lng1, double lat2, double lng2);
}
