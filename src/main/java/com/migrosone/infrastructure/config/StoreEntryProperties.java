package com.migrosone.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "store.entry")
public class StoreEntryProperties {

    private double radiusMeters;
    private long reentryLimitSeconds;

}
