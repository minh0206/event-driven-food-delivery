package com.fooddelivery.deliveryservice.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.deliveryservice.dto.LocationUpdateRequestDto;
import com.fooddelivery.deliveryservice.dto.UpdateStatusRequestDto;
import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.model.DriverStatus;
import com.fooddelivery.deliveryservice.service.DriverService;
import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.shared.dto.DriverOrderDto;
import com.fooddelivery.shared.dto.OrderItemDto;
import com.fooddelivery.shared.enumerate.OrderStatus;

@WebMvcTest(DriverController.class)
@TestPropertySource(properties = {
        "logging.level.org.springframework.security=INFO",
        "logging.level.org.springframework.web=INFO",
        "order-service.url=http://localhost:8083",
        "user-service.url=http://localhost:8081",
        "restaurant-service.url=http://localhost:8082"
})
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

    @MockBean
    private JwtService jwtService;

    private Driver testDriver;
    private DriverOrderDto testOrderDto;

    @BeforeEach
    void setup() {
        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setUserId(100L);
        testDriver.setStatus(DriverStatus.AVAILABLE);
        testDriver.setCurrentLatitude(40.7128);
        testDriver.setCurrentLongitude(-74.0060);
        testDriver.setCurrentOrderId(10L);

        OrderItemDto itemDto = new OrderItemDto(5L, 2, new BigDecimal("25.00"));
        testOrderDto = new DriverOrderDto(
                10L,
                300L,
                200L,
                OrderStatus.DRIVER_ASSIGNED,
                new BigDecimal("50.00"),
                List.of(itemDto),
                LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void getDriverStatus_returnsCurrentStatus() throws Exception {
        // Arrange
        when(driverService.getDriverStatus(100L)).thenReturn("AVAILABLE");

        // Act & Assert
        mockMvc.perform(get("/api/drivers/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));

        verify(driverService, times(1)).getDriverStatus(100L);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void updateStatus_withValidRequest_updatesDriverStatus() throws Exception {
        // Arrange
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(DriverStatus.AVAILABLE);
        testDriver.setStatus(DriverStatus.AVAILABLE);

        when(driverService.updateDriverStatus(100L, DriverStatus.AVAILABLE)).thenReturn(testDriver);

        // Act & Assert
        mockMvc.perform(put("/api/drivers/status").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));

        verify(driverService, times(1)).updateDriverStatus(100L, DriverStatus.AVAILABLE);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void updateStatus_toOffline_updatesSuccessfully() throws Exception {
        // Arrange
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(DriverStatus.OFFLINE);
        testDriver.setStatus(DriverStatus.OFFLINE);

        when(driverService.updateDriverStatus(100L, DriverStatus.OFFLINE)).thenReturn(testDriver);

        // Act & Assert
        mockMvc.perform(put("/api/drivers/status").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OFFLINE"));

        verify(driverService, times(1)).updateDriverStatus(100L, DriverStatus.OFFLINE);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void updateStatus_toOnDelivery_updatesSuccessfully() throws Exception {
        // Arrange
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(DriverStatus.ON_DELIVERY);
        testDriver.setStatus(DriverStatus.ON_DELIVERY);

        when(driverService.updateDriverStatus(100L, DriverStatus.ON_DELIVERY)).thenReturn(testDriver);

        // Act & Assert
        mockMvc.perform(put("/api/drivers/status").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_DELIVERY"));

        verify(driverService, times(1)).updateDriverStatus(100L, DriverStatus.ON_DELIVERY);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void getDriverOrder_returnsOrderDetails() throws Exception {
        // Arrange
        when(driverService.getDriverOrder(100L)).thenReturn(testOrderDto);

        // Act & Assert
        mockMvc.perform(get("/api/drivers/order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.restaurantId").value(200))
                .andExpect(jsonPath("$.customerId").value(300))
                .andExpect(jsonPath("$.status").value("DRIVER_ASSIGNED"))
                .andExpect(jsonPath("$.totalPrice").value(50.00));

        verify(driverService, times(1)).getDriverOrder(100L);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void markOrderAsInTransit_marksSuccessfully() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/drivers/order/pickup").with(csrf()))
                .andExpect(status().isOk());

        verify(driverService, times(1)).markOrderAsInTransit(100L);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void markOrderAsCompleted_completesSuccessfully() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/drivers/order/complete").with(csrf()))
                .andExpect(status().isOk());

        verify(driverService, times(1)).markOrderAsDelivered(100L);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void updateLocation_withValidCoordinates_updatesSuccessfully() throws Exception {
        // Arrange
        LocationUpdateRequestDto requestDto = new LocationUpdateRequestDto(40.7128, -74.0060);

        // Act & Assert
        mockMvc.perform(post("/api/drivers/location").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(driverService, times(1)).updateDriverLocation(100L, 40.7128, -74.0060);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void updateLocation_withDifferentCoordinates_updatesSuccessfully() throws Exception {
        // Arrange
        LocationUpdateRequestDto requestDto = new LocationUpdateRequestDto(51.5074, -0.1278);

        // Act & Assert
        mockMvc.perform(post("/api/drivers/location").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(driverService, times(1)).updateDriverLocation(100L, 51.5074, -0.1278);
    }

    @Test
    @WithMockUser(username = "101", roles = "DELIVERY_DRIVER")
    void getDriverStatus_extractsUserIdFromPrincipal() throws Exception {
        // Arrange
        when(driverService.getDriverStatus(101L)).thenReturn("OFFLINE");

        // Act & Assert
        mockMvc.perform(get("/api/drivers/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OFFLINE"));

        verify(driverService, times(1)).getDriverStatus(101L);
    }

    @Test
    @WithMockUser(username = "102", roles = "DELIVERY_DRIVER")
    void updateStatus_extractsUserIdFromPrincipal() throws Exception {
        // Arrange
        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto(DriverStatus.AVAILABLE);
        Driver driver = new Driver();
        driver.setId(2L);
        driver.setUserId(102L);
        driver.setStatus(DriverStatus.AVAILABLE);

        when(driverService.updateDriverStatus(102L, DriverStatus.AVAILABLE)).thenReturn(driver);

        // Act & Assert
        mockMvc.perform(put("/api/drivers/status").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(driverService, times(1)).updateDriverStatus(102L, DriverStatus.AVAILABLE);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void updateLocation_withNegativeLatitude_updatesSuccessfully() throws Exception {
        // Arrange
        LocationUpdateRequestDto requestDto = new LocationUpdateRequestDto(-33.8688, 151.2093);

        // Act & Assert
        mockMvc.perform(post("/api/drivers/location").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(driverService, times(1)).updateDriverLocation(100L, -33.8688, 151.2093);
    }

    @Test
    @WithMockUser(username = "100", roles = "DELIVERY_DRIVER")
    void updateLocation_withZeroCoordinates_updatesSuccessfully() throws Exception {
        // Arrange
        LocationUpdateRequestDto requestDto = new LocationUpdateRequestDto(0.0, 0.0);

        // Act & Assert
        mockMvc.perform(post("/api/drivers/location").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(driverService, times(1)).updateDriverLocation(100L, 0.0, 0.0);
    }
}
