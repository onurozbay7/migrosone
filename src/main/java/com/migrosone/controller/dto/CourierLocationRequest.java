package com.migrosone.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Schema(description = "Courier location request payload")
public class CourierLocationRequest {

    @NotBlank
    @Schema(description = "Unique ID of the courier", example = "courier_123")
    private String courierId;

    @NotNull
    @Schema(description = "Latitude value", example = "40.9923")
    private Double lat;

    @NotNull
    @Schema(description = "Longitude value", example = "29.1244")
    private Double lng;

    @NotNull
    @Schema(description = "Location timestamp", example = "2024-06-20T14:33:00")
    private LocalDateTime timestamp;
}