package com.migrosone.application.event;

public interface StoreEntryEventPublisher {
    void publish(StoreEntryEvent event);
}
