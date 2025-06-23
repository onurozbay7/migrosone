package com.migrosone.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migrosone.controller.dto.CourierLocationRequest;
import com.migrosone.domain.model.StoreEntryLog;
import com.migrosone.infrastructure.repository.StoreLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CourierIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreLogRepository storeLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        storeLogRepository.deleteAll();
    }

    @Test
    void shouldProcessLocationAndCreateLogWhenNearStore() throws Exception {
        // given
        CourierLocationRequest request = new CourierLocationRequest(
                "courier123",
                40.992331,
                29.124423,
                LocalDateTime.now()
        );

        // when
        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Location received successfully"));

        // then
        List<StoreEntryLog> logs = storeLogRepository.findAll();
        assertThat(logs).hasSize(1);
        StoreEntryLog log = logs.get(0);
        assertThat(log.getCourierId()).isEqualTo("courier123");
        assertThat(log.getStoreName()).isEqualTo("Ataşehir MMM Migros");
    }

    @Test
    void shouldLogEntryOnlyOnceWithinReentryLimit() throws Exception {
        String courierId = "courier1";
        double lat = 40.9923307;
        double lng = 29.1244229;
        LocalDateTime firstEntryTime = LocalDateTime.now();

        CourierLocationRequest firstRequest = new CourierLocationRequest(courierId, lat, lng, firstEntryTime);
        CourierLocationRequest secondRequest = new CourierLocationRequest(courierId, lat, lng, firstEntryTime.plusSeconds(30)); // 30 < 60

        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isOk());

        List<StoreEntryLog> logs = storeLogRepository.findAll();
        assertThat(logs).hasSize(1);
        StoreEntryLog log = logs.get(0);
        assertThat(log.getCourierId()).isEqualTo("courier1");
        assertThat(log.getStoreName()).isEqualTo("Ataşehir MMM Migros");
    }

    @Test
    void shouldRecordNewEntryIfReentryLimitExceeded() throws Exception {
        // given
        String courierId = "courier-456";

        CourierLocationRequest firstEntry = new CourierLocationRequest(
                courierId,
                40.986106,  // near Novada MMM Migros
                29.116129,
                LocalDateTime.of(2025, 1, 1, 12, 0, 0)
        );

        CourierLocationRequest secondEntry = new CourierLocationRequest(
                courierId,
                40.986106,
                29.116129,
                LocalDateTime.of(2025, 1, 1, 12, 2, 0)  // 2 minutes later
        );

        // when
        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstEntry)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondEntry)))
                .andExpect(status().isOk());

        // then
        List<StoreEntryLog> entries = storeLogRepository.findAll();
        assertThat(entries).hasSize(2);
        assertThat(entries).extracting(StoreEntryLog::getCourierId)
                .containsOnly(courierId);
    }

    @Test
    void shouldNotLogEntryIfCourierIsFarFromAllStores() throws Exception {
        String courierId = "far-courier";
        CourierLocationRequest request = new CourierLocationRequest(
                courierId,
                39.92077,
                32.85411,
                LocalDateTime.of(2025, 1, 1, 15, 0, 0)
        );

        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Location received successfully"));

        List<StoreEntryLog> logs = storeLogRepository.findAll();
        assertThat(logs).isEmpty();
    }

    @Test
    void shouldCalculateTotalDistanceCorrectly() throws Exception {
        String courierId = "distance-courier";

        CourierLocationRequest first = new CourierLocationRequest(
                courierId,
                40.9923307,
                29.1244229,
                LocalDateTime.of(2025, 1, 1, 10, 0, 0)
        );

        CourierLocationRequest second = new CourierLocationRequest(
                courierId,
                41.0000000,
                29.1340000,
                LocalDateTime.of(2025, 1, 1, 10, 2, 0)
        );

        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/couriers/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isOk());


        mockMvc.perform(get("/api/couriers/{courierId}/distance", courierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Distance fetched successfully"))
                .andExpect(jsonPath("$.data").isNumber())
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.closeTo(1171.86, 20.0))); // 1171 ± 20 meters
    }
}
