package com.migrosone.application.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
@RequiredArgsConstructor
@Getter
public class StoreEntryEvent {

    private final String courierId;
    private final String storeName;
    private final LocalDateTime entryTime;
}
