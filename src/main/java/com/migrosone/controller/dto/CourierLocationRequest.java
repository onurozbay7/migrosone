package com.migrosone.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class CourierLocationRequest {

    @NotBlank
    private String courierId;

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    @NotNull
    private LocalDateTime timestamp;
}