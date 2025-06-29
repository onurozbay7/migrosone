package com.migrosone.controller;

import com.migrosone.controller.dto.ApiResponse;
import com.migrosone.controller.dto.CourierLocationRequest;
import com.migrosone.domain.model.CourierLocation;
import com.migrosone.application.service.CourierService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/couriers")
public class CourierController {

    private final CourierService courierService;
    private final ModelMapper modelMapper;

    @Operation(
            summary = "Send courier location",
            description = "Receives current location of a courier and logs entry if it's near a store"
    )
    @PostMapping("/location")
    public ResponseEntity<ApiResponse<Void>> sendLocation(@Valid @RequestBody CourierLocationRequest request) {
        CourierLocation location = modelMapper.map(request, CourierLocation.class);
        courierService.processLocation(location);
        return ResponseEntity.ok(new ApiResponse<>("Location received successfully"));
    }

    @Operation(
            summary = "Get total distance for a courier",
            description = "Returns the total distance traveled by a courier since tracking started"
    )
    @GetMapping("/{courierId}/distance")
    public ResponseEntity<ApiResponse<Double>> getTotalTravelDistance(@PathVariable String courierId) {
        double distance = courierService.getTotalTravelDistance(courierId);
        return ResponseEntity.ok(new ApiResponse<>("Distance fetched successfully", distance));
    }
}
