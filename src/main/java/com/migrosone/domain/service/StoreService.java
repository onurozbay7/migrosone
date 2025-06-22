package com.migrosone.domain.service;

import com.migrosone.domain.model.Store;
import com.migrosone.domain.model.CourierLocation;

import java.util.List;

public interface StoreService {

    List<Store> getStoresInRadius(CourierLocation location, double radius);
}
