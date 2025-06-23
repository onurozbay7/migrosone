package com.migrosone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migrosone.application.exception.CourierNotFoundException;
import com.migrosone.application.service.CourierService;
import com.migrosone.controller.dto.CourierLocationRequest;
import com.migrosone.domain.model.CourierLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;

import static org.mockito.Mockito.*;

@WebMvcTest(CourierController.class)
@Import(CourierControllerTest.TestConfig.class)
class CourierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourierService courierService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public CourierService courierService() {
            return mock(CourierService.class);
        }

        @Bean
        public ModelMapper modelMapper() {
            return mock(ModelMapper.class);
        }
    }

    @AfterEach
    void resetMocks() {
        reset(courierService);
    }

    @Test
    void shouldReturnOkWhenLocationIsSent() throws Exception {
        // given
        CourierLocationRequest request = new CourierLocationRequest("courier123", 40.0, 29.0, LocalDateTime.now());
        CourierLocation location = new CourierLocation("courier123", 40.0, 29.0, request.getTimestamp());

        when(modelMapper.map(any(CourierLocationRequest.class), eq(CourierLocation.class))).thenReturn(location);

        // when
        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Location received successfully"));

        verify(courierService).processLocation(location);
    }

    @Test
    void shouldReturnTotalDistanceForCourier() throws Exception {
        // given
        String courierId = "courier123";
        double distance = 1250.5;

        when(courierService.getTotalTravelDistance(courierId)).thenReturn(distance);

        // when
        mockMvc.perform(get("/api/couriers/{courierId}/distance", courierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Distance fetched successfully"))
                .andExpect(jsonPath("$.data").value(distance));
    }

    @Test
    void shouldReturnNotFoundWhenCourierDoesNotExist() throws Exception {
        // given
        String unknownCourierId = "courier5";
        when(courierService.getTotalTravelDistance(unknownCourierId))
                .thenThrow(new CourierNotFoundException(unknownCourierId));

        // when
        mockMvc.perform(get("/api/couriers/{courierId}/distance", unknownCourierId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Courier with id 'courier5' not found."));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        CourierLocationRequest invalidRequest = new CourierLocationRequest();
        invalidRequest.setCourierId("");
        invalidRequest.setLat(null);
        invalidRequest.setLng(null);
        invalidRequest.setTimestamp(null);

        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.courierId").exists())
                .andExpect(jsonPath("$.lat").exists())
                .andExpect(jsonPath("$.lng").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnMethodNotAllowedForWrongHttpMethod() throws Exception {
        mockMvc.perform(get("/api/couriers/location"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error").value("HTTP method not allowed for this endpoint"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() throws Exception {
        when(courierService.getTotalTravelDistance(anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/couriers/test-id/distance"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}