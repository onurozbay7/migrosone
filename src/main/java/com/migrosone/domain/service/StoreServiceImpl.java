package com.migrosone.domain.service;

import com.migrosone.domain.model.Store;
import com.migrosone.domain.model.CourierLocation;
import com.migrosone.infrastructure.loader.StoreLoader;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    private final DistanceCalculator distanceCalculator;
    private final List<Store> storeList;

    public StoreServiceImpl(DistanceCalculator distanceCalculator, StoreLoader storeLoader) {
        this.distanceCalculator = distanceCalculator;
        this.storeList = storeLoader.loadStores();
    }

    @Override
    public List<Store> getStoresInRadius(CourierLocation location, double radius) {
        List<Store> inRadius = new ArrayList<>();

        for (Store store : storeList) {
            double distance = distanceCalculator.calculate(location.getLat(), location.getLng(), store.getLat(), store.getLng());

            if (distance <= radius) {
                inRadius.add(store);
            }
        }
        return inRadius;
    }
}
