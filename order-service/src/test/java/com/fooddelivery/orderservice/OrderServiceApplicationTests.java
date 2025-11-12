package com.fooddelivery.orderservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.fooddelivery.orderservice.mapper.OrderMapper;
import com.fooddelivery.shared.publisher.OrderPlacedEventPublisher;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceApplicationTests {
    @MockBean
    private OrderPlacedEventPublisher orderPlacedEventPublisher;

    @MockBean
    private OrderMapper orderMapper;

    // @Test
    // void contextLoads() {
    // // Test that the application context loads successfully
    // }

}
