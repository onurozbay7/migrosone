package com.migrosone.application.service;

import com.migrosone.application.event.StoreEntryEvent;
import com.migrosone.application.event.StoreEntryEventPublisher;
import com.migrosone.application.exception.CourierNotFoundException;
import com.migrosone.domain.model.CourierLocation;
import com.migrosone.domain.model.CourierState;
import com.migrosone.domain.model.Store;
import com.migrosone.infrastructure.config.StoreEntryProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierServiceImplTest {

    @Mock
    private StoreService storeService;
    @Mock
    private DistanceCalculator distanceCalculator;
    @Mock
    private StoreEntryEventPublisher eventPublisher;
    @Mock
    private StoreEntryProperties storeEntryProperties;

    @InjectMocks
    private CourierServiceImpl courierService;


    @Test
    void getTotalTravelDistance_shouldReturnCorrectValue() throws Exception {

        CourierState state = new CourierState();
        state.setTotalDistance(300.0);


        Field field = CourierServiceImpl.class.getDeclaredField("courierStates");
        field.setAccessible(true);

        Map<String, CourierState> map = new HashMap<>();
        map.put("courier-1", state);
        field.set(courierService, map);

        double result = courierService.getTotalTravelDistance("courier-1");

        assertThat(result).isEqualTo(300.0);
    }

    @Test
    void shouldThrowExceptionWhenCourierNotFound() {
        // given
        String unknownCourierId = "courier_5";

        // when
        CourierNotFoundException exception = assertThrows(
                CourierNotFoundException.class,
                () -> courierService.getTotalTravelDistance(unknownCourierId)
        );

        assertThat(exception.getMessage()).isEqualTo("Courier with id '" + unknownCourierId + "' not found.");
    }

    @Test
    void shouldUpdateTotalDistanceWhenLocationChanges() {
        // given
        String courierId = "courier-42";
        CourierLocation firstLocation = new CourierLocation(courierId, 40.0, 29.0, LocalDateTime.now());
        CourierLocation secondLocation = new CourierLocation(courierId, 40.001, 29.001, LocalDateTime.now().plusMinutes(1));


        when(distanceCalculator.calculate(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(150.0);

        // when
        courierService.processLocation(firstLocation);
        courierService.processLocation(secondLocation);

        // then
        verify(distanceCalculator).calculate(
                eq(40.0), eq(29.0),
                eq(40.001), eq(29.001)
        );

        double totalDistance = courierService.getTotalTravelDistance(courierId);
        assertThat(totalDistance).isEqualTo(150.0);
    }

    @Test
    void shouldNotLogStoreEntryIfWithinReentryLimit() {
        // given
        String courierId = "courier-1";
        String storeName = "Test Store";

        LocalDateTime now = LocalDateTime.now();
        CourierLocation firstLocation = new CourierLocation(courierId, 40.0, 29.0, now);
        CourierLocation secondLocation = new CourierLocation(courierId, 40.0, 29.0, now.plusSeconds(30)); // < 60s

        Store store = new Store(storeName, 40.0, 29.0);
        when(storeService.getStoresInRadius(any(), eq(100.0))).thenReturn(List.of(store));
        when(storeEntryProperties.getRadiusMeters()).thenReturn(100.0);
        when(storeEntryProperties.getReentryLimitSeconds()).thenReturn(60L);

        // when
        courierService.processLocation(firstLocation);
        courierService.processLocation(secondLocation);

        // then
        verify(eventPublisher, times(1)).publish(any(StoreEntryEvent.class)); // only first entry
    }

    @Test
    void shouldLogStoreEntryIfAfterReentryLimit() {
        // given
        String courierId = "courier-2";
        String storeName = "Test Store";

        LocalDateTime now = LocalDateTime.now();
        CourierLocation first = new CourierLocation(courierId, 40.0, 29.0, now);
        CourierLocation second = new CourierLocation(courierId, 40.0, 29.0, now.plusMinutes(2)); // > 60s

        Store store = new Store(storeName, 40.0, 29.0);
        when(storeService.getStoresInRadius(any(), eq(100.0))).thenReturn(List.of(store));
        when(storeEntryProperties.getRadiusMeters()).thenReturn(100.0);
        when(storeEntryProperties.getReentryLimitSeconds()).thenReturn(60L);

        // when
        courierService.processLocation(first);
        courierService.processLocation(second);

        // then
        verify(eventPublisher, times(2)).publish(any(StoreEntryEvent.class)); // both entries logged
    }

    @Test
    void shouldLogMultipleStoreEntriesIfMultipleStoresInRadius() {
        // given
        String courierId = "courier-3";
        LocalDateTime now = LocalDateTime.now();
        CourierLocation location = new CourierLocation(courierId, 40.0, 29.0, now);

        Store store1 = new Store("Store A", 40.0, 29.0);
        Store store2 = new Store("Store B", 40.0, 29.0);

        when(storeService.getStoresInRadius(any(), anyDouble())).thenReturn(List.of(store1, store2));

        // when
        courierService.processLocation(location);

        // then
        verify(eventPublisher, times(2)).publish(any(StoreEntryEvent.class)); // both entries logged
    }
}