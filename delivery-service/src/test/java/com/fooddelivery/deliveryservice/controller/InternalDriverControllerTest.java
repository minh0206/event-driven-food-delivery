package com.fooddelivery.deliveryservice.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.model.DriverStatus;
import com.fooddelivery.deliveryservice.service.DriverService;
import com.fooddelivery.securitylib.service.JwtService;

@WebMvcTest(InternalDriverController.class)
class InternalDriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;

    @MockBean
    private JwtService jwtService;

    private Driver testDriver;

    @BeforeEach
    void setup() {
        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setUserId(100L);
        testDriver.setStatus(DriverStatus.OFFLINE);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void createDriver_withValidRequest_returnsDriverId() throws Exception {
        // Arrange
        when(driverService.createDriver(100L)).thenReturn(testDriver);

        // Act & Assert
        mockMvc.perform(post("/internal/drivers").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value(1));

        verify(driverService, times(1)).createDriver(100L);
    }

    @Test
    @WithMockUser(username = "101", roles = "DELIVERY_DRIVER")
    void createDriver_extractsUserIdFromPrincipal() throws Exception {
        // Arrange
        Driver newDriver = new Driver();
        newDriver.setId(2L);
        newDriver.setUserId(101L);
        newDriver.setStatus(DriverStatus.OFFLINE);

        when(driverService.createDriver(101L)).thenReturn(newDriver);

        // Act & Assert
        mockMvc.perform(post("/internal/drivers").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value(2));

        verify(driverService, times(1)).createDriver(101L);
    }

    @Test
    @WithMockUser(username = "200", roles = "DELIVERY_DRIVER")
    void createDriver_withDifferentUserId_createsSuccessfully() throws Exception {
        // Arrange
        Driver newDriver = new Driver();
        newDriver.setId(10L);
        newDriver.setUserId(200L);
        newDriver.setStatus(DriverStatus.OFFLINE);

        when(driverService.createDriver(200L)).thenReturn(newDriver);

        // Act & Assert
        mockMvc.perform(post("/internal/drivers").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value(10));

        verify(driverService, times(1)).createDriver(200L);
    }
}
