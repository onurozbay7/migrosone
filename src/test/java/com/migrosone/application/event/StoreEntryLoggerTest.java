package com.migrosone.application.event;

import com.migrosone.domain.model.StoreEntryLog;
import com.migrosone.infrastructure.repository.StoreLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

class StoreEntryLoggerTest {

    private StoreLogRepository logRepository;
    private StoreEntryLogger logger;

    @BeforeEach
    void setUp() {
        logRepository = mock(StoreLogRepository.class);
        logger = new StoreEntryLogger(logRepository);
    }

    @Test
    void shouldSaveStoreEntryLogWhenEventIsReceived() {
        // given
        String courierId = "courier1";
        String storeName = "Store A";
        LocalDateTime entryTime = LocalDateTime.now();
        StoreEntryEvent event = new StoreEntryEvent(courierId, storeName, entryTime);

        // when
        logger.onEvent(event);

        // then
        ArgumentCaptor<StoreEntryLog> captor = ArgumentCaptor.forClass(StoreEntryLog.class);
        verify(logRepository).save(captor.capture());

        StoreEntryLog savedLog = captor.getValue();
        assertThat(savedLog.getCourierId()).isEqualTo(courierId);
        assertThat(savedLog.getStoreName()).isEqualTo(storeName);
        assertThat(savedLog.getEntryTime()).isEqualTo(entryTime);
    }
}