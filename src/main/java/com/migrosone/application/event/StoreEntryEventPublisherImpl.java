package com.migrosone.application.event;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StoreEntryEventPublisherImpl implements StoreEntryEventPublisher {

    private final List<StoreEntryEventListener> listeners;

    public StoreEntryEventPublisherImpl(List<StoreEntryEventListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void publish(StoreEntryEvent event) {
        for (StoreEntryEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
