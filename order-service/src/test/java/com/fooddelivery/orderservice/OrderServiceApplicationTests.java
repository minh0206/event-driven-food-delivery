package com.fooddelivery.orderservice;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import com.fooddelivery.shared.publisher.OrderPlacedEventPublisher;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        @Primary
        OrderPlacedEventPublisher orderPlacedEventPublisher() {
            return mock(OrderPlacedEventPublisher.class);
        }
    }

}
