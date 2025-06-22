package com.migrosone.domain.service;

import com.migrosone.application.event.StoreEntryEvent;
import com.migrosone.application.event.StoreEntryEventPublisher;
import com.migrosone.application.exception.CourierNotFoundException;
import com.migrosone.domain.model.CourierLocation;
import com.migrosone.domain.model.CourierState;
import com.migrosone.domain.model.Store;
import com.migrosone.infrastructure.config.StoreEntryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CourierServiceImpl implements CourierService {

    private final StoreService storeService;
    private final DistanceCalculator distanceCalculator;
    private final StoreEntryEventPublisher eventPublisher;
    private final StoreEntryProperties storeEntryProperties;
    private final Map<String, CourierState> courierStates = new HashMap<>();

    @Override
    public void processLocation(CourierLocation location) {
        String courierId = location.getCourierId();
        CourierState state = courierStates.computeIfAbsent(courierId, id -> new CourierState());

        updateCourierStateWithDistance(location, state);
        updateLastLocation(location, state);
        handleStoreEntryIfInRadius(location, state);
    }

    private void updateCourierStateWithDistance(CourierLocation location, CourierState state) {
        CourierLocation previousLocation = state.getLastLocation();
        if (previousLocation != null) {
            double distance = distanceCalculator.calculate(
                    previousLocation.getLat(), previousLocation.getLng(),
                    location.getLat(), location.getLng()
            );
            state.setTotalDistance(state.getTotalDistance() + distance);
        }
    }

    private void updateLastLocation(CourierLocation location, CourierState state) {
        state.setLastLocation(location);
    }

    private void handleStoreEntryIfInRadius(CourierLocation location, CourierState state) {
        List<Store> nearbyStores = storeService.getStoresInRadius(location, storeEntryProperties.getRadiusMeters());
        for (Store store : nearbyStores) {
            if (isEligibleForEntry(location, state, store)) {
                state.getLastStoreEntryTimes().put(store.getName(), location.getTimestamp());
                publishStoreEntryEvent(location, store);
            }
        }
    }

    private boolean isEligibleForEntry(CourierLocation location, CourierState state, Store store) {
        LocalDateTime lastEntryTime = state.getLastStoreEntryTimes().get(store.getName());

        if (lastEntryTime == null) return true;

        long timeSinceLastEntry = Duration.between(lastEntryTime, location.getTimestamp()).getSeconds();
        return timeSinceLastEntry > storeEntryProperties.getReentryLimitSeconds();
    }

    private void publishStoreEntryEvent(CourierLocation location, Store store) {
        StoreEntryEvent event = new StoreEntryEvent(
                location.getCourierId(),
                store.getName(),
                location.getTimestamp()
        );
        eventPublisher.publish(event);
    }

    @Override
    public double getTotalTravelDistance(String courierId) {
        return Optional.ofNullable(courierStates.get(courierId))
                .orElseThrow(() -> new CourierNotFoundException(courierId))
                .getTotalDistance();
    }
}