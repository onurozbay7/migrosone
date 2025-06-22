package com.migrosone.application.event;

import com.migrosone.domain.model.StoreEntryLog;
import com.migrosone.infrastructure.repository.StoreLogRepository;
import org.springframework.stereotype.Component;

@Component
public class StoreEntryLogger implements StoreEntryEventListener {

    private final StoreLogRepository logRepository;

    public StoreEntryLogger(StoreLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void onEvent(StoreEntryEvent event) {
        StoreEntryLog log = new StoreEntryLog();
        log.setCourierId(event.getCourierId());
        log.setStoreName(event.getStoreName());
        log.setEntryTime(event.getEntryTime());

        logRepository.save(log);
    }
}