package com.migrosone.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierLocation {
    private String courierId;
    private double lat;
    private double lng;
    private LocalDateTime timestamp;
}