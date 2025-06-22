package com.migrosone.infrastructure.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.migrosone.domain.model.Store;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonStoreLoader implements StoreLoader {

    @Value("classpath:stores.json")
    private Resource storesResource;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Store> storeList = new ArrayList<>();

    @PostConstruct
    public void init() throws IOException {
        storeList = objectMapper.readValue(storesResource.getInputStream(), new TypeReference<>() {});
    }

    @Override
    public List<Store> loadStores() {
        return storeList;
    }
}
