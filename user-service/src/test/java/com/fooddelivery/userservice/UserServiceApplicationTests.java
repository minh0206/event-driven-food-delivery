package com.fooddelivery.userservice;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.fooddelivery.userservice.mapper.UserMapper;

@SpringBootTest
@Import(UserServiceApplicationTests.MockingConfiguration.class)
@ActiveProfiles("test")
class UserServiceApplicationTests {
    @TestConfiguration
    static class MockingConfiguration {
        @Bean
        UserMapper userMapper() {
            return Mockito.mock(UserMapper.class);
        }
    }

    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
    }
}