package com.fooddelivery.deliveryservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.fooddelivery.deliveryservice.mapper.DriverMapper;
import com.fooddelivery.deliveryservice.service.OrderEventListener;
import com.fooddelivery.shared.publisher.DriverLocationUpdateEventPublisher;
import com.fooddelivery.shared.publisher.OrderDeliveredEventPublisher;
import com.fooddelivery.shared.publisher.OrderInTransitEventPublisher;

@SpringBootTest
@ActiveProfiles("test")
class DeliveryServiceApplicationTests {
    @MockBean
    private DriverMapper driverMapper;

    @MockBean
    private DriverLocationUpdateEventPublisher driverLocationUpdateEventPublisher;

    @MockBean
    private OrderInTransitEventPublisher orderInTransitEventPublisher;

    @MockBean
    private OrderDeliveredEventPublisher orderDeliveredEventPublisher;

    @MockBean
    private OrderEventListener orderEventListener;

    // @Test
    // void contextLoads() {
    // // Test that the application context loads successfully
    // }
}
