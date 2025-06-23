package com.migrosone.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StoreEntryEvent {

    private final String courierId;
    private final String storeName;
    private final LocalDateTime entryTime;
}
