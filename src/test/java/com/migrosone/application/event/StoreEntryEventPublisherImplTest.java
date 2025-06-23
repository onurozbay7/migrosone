package com.migrosone.application.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoreEntryEventPublisherImplTest {

    @Mock
    StoreEntryEventListener listener;

    StoreEntryEventPublisherImpl publisher;

    @BeforeEach
    void setUp() {
        publisher = new StoreEntryEventPublisherImpl(List.of(listener));
    }

    @Test
    void shouldNotifyAllListeners() {
        StoreEntryEvent event = new StoreEntryEvent("courier123", "StoreX", LocalDateTime.now());

        publisher.publish(event);

        verify(listener).onEvent(event);
    }
}