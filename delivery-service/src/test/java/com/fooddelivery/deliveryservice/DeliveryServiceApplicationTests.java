package com.fooddelivery.deliveryservice;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import com.fooddelivery.deliveryservice.service.OrderEventListener;
import com.fooddelivery.shared.publisher.DriverLocationUpdateEventPublisher;
import com.fooddelivery.shared.publisher.OrderDeliveredEventPublisher;
import com.fooddelivery.shared.publisher.OrderInTransitEventPublisher;

@SpringBootTest
@ActiveProfiles("test")
class DeliveryServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        @Primary
        DriverLocationUpdateEventPublisher driverLocationUpdateEventPublisher() {
            return mock(DriverLocationUpdateEventPublisher.class);
        }

        @Bean
        @Primary
        OrderInTransitEventPublisher orderInTransitEventPublisher() {
            return mock(OrderInTransitEventPublisher.class);
        }

        @Bean
        @Primary
        OrderDeliveredEventPublisher orderDeliveredEventPublisher() {
            return mock(OrderDeliveredEventPublisher.class);
        }

        @Bean
        @Primary
        OrderEventListener orderEventListener() {
            return mock(OrderEventListener.class);
        }
    }

}
