package com.migrosone.application.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HaversineCalculatorTest {

    private final HaversineCalculator calculator = new HaversineCalculator();

    @Test
    void shouldReturnZeroDistanceForSameCoordinates() {
        double distance = calculator.calculate(40.0, 29.0, 40.0, 29.0);
        assertThat(distance).isEqualTo(0.0);
    }

    @Test
    void shouldCalculateCorrectDistanceBetweenTwoPoints() {
        double distance = calculator.calculate(40.9890, 29.0300, 41.0430, 29.0094);

        assertThat(distance).isGreaterThan(6200);
        assertThat(distance).isLessThan(6300);
    }

    @Test
    void shouldBeSymmetricForLatLngOrder() {
        double d1 = calculator.calculate(40.0, 29.0, 41.0, 30.0);
        double d2 = calculator.calculate(41.0, 30.0, 40.0, 29.0);

        assertThat(d1).isEqualTo(d2);
    }
}