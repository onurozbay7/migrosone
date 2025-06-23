package com.migrosone.application.service;

import com.migrosone.domain.model.CourierLocation;
import com.migrosone.domain.model.Store;
import com.migrosone.infrastructure.loader.StoreLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StoreServiceImplTest {

    private DistanceCalculator distanceCalculator;
    private StoreServiceImpl storeService;

    @BeforeEach
    void setUp() {
        distanceCalculator = mock(DistanceCalculator.class);
        StoreLoader storeLoader = mock(StoreLoader.class);

        List<Store> mockStores = Arrays.asList(
                new Store("Store1", 40.0, 29.0),
                new Store("Store2", 41.0, 30.0)
        );

        when(storeLoader.loadStores()).thenReturn(mockStores);

        storeService = new StoreServiceImpl(distanceCalculator, storeLoader);
    }

    @Test
    void shouldReturnStoresWithinGivenRadius() {
        CourierLocation courierLocation = new CourierLocation("courier1", 40.0, 29.0, LocalDateTime.now());

        when(distanceCalculator.calculate(anyDouble(), anyDouble(), eq(40.0), eq(29.0))).thenReturn(50.0);
        when(distanceCalculator.calculate(anyDouble(), anyDouble(), eq(41.0), eq(30.0))).thenReturn(2000.0);

        List<Store> result = storeService.getStoresInRadius(courierLocation, 100.0);

        assertThat(result)
                .hasSize(1)
                .extracting(Store::getName)
                .containsExactly("Store1");
    }

    @Test
    void shouldReturnEmptyListIfNoStoreWithinRadius() {
        CourierLocation courierLocation = new CourierLocation("courier1", 40.0, 29.0, LocalDateTime.now());

        when(distanceCalculator.calculate(anyDouble(), anyDouble(), eq(40.0), eq(29.0))).thenReturn(150.0);
        when(distanceCalculator.calculate(anyDouble(), anyDouble(), eq(41.0), eq(30.0))).thenReturn(2000.0);

        List<Store> result = storeService.getStoresInRadius(courierLocation, 100.0);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnAllStoresIfAllWithinRadius() {
        CourierLocation courierLocation = new CourierLocation("courier1", 40.0, 29.0, LocalDateTime.now());

        when(distanceCalculator.calculate(anyDouble(), anyDouble(), eq(40.0), eq(29.0))).thenReturn(20.0);
        when(distanceCalculator.calculate(anyDouble(), anyDouble(), eq(41.0), eq(30.0))).thenReturn(80.0);

        List<Store> result = storeService.getStoresInRadius(courierLocation, 100.0);

        assertThat(result).hasSize(2);
    }
}