package com.fooddelivery.userservice.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.shared.feignclient.DeliveryServiceClient;
import com.fooddelivery.shared.feignclient.RestaurantServiceClient;
import com.fooddelivery.userservice.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.cloud.discovery.enabled=false",
})
/*
 * Note: @MockBean deprecation warnings are from newer Spring Boot versions but
 * functionality remains intact. Null type safety warnings are common in test
 * code
 * and don't affect test execution.
 */
class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantServiceClient restaurantServiceClient;

    @MockBean
    private DeliveryServiceClient deliveryServiceClient;

    @MockBean
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    // @Test
    // void registerCustomer_endToEnd_createsUserAndReturnsToken() throws Exception
    // {
    // RegisterRequestDto req = new RegisterRequestDto(
    // "integration@test.com",
    // "password123",
    // "Integration",
    // "Test",
    // "",
    // "",
    // "");

    // when(jwtService.generateToken(any(), any())).thenReturn("integration-token");

    // mockMvc.perform(post("/api/users/register/customer")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(objectMapper.writeValueAsString(req)))
    // .andExpect(status().isOk())
    // .andExpect(jsonPath("$.token").value("integration-token"))
    // .andReturn();

    // // Verify user was created in database
    // assertTrue(userRepository.findByEmail("integration@test.com").isPresent());
    // User savedUser = userRepository.findByEmail("integration@test.com").get();
    // assertEquals("Integration", savedUser.getFirstName());
    // assertEquals(Role.CUSTOMER, savedUser.getRole());
    // }

    // @Test
    // void registerRestaurantAdmin_endToEnd_createsUserWithRestaurantId() throws
    // Exception {
    // RegisterRequestDto req = new RegisterRequestDto(
    // "restaurant@test.com",
    // "password123",
    // "Restaurant",
    // "Owner",
    // "Test Restaurant",
    // "123 Test St",
    // "Italian");

    // when(restaurantServiceClient.createRestaurant(any())).thenReturn(Map.of("restaurantId",
    // 999L));
    // when(jwtService.generateToken(any(), any())).thenReturn("restaurant-token");

    // mockMvc.perform(post("/api/users/register/restaurant")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(objectMapper.writeValueAsString(req)))
    // .andExpect(status().isOk())
    // .andExpect(jsonPath("$.token").value("restaurant-token"));

    // // Verify user was created with restaurant ID
    // assertTrue(userRepository.findByEmail("restaurant@test.com").isPresent());
    // User savedUser = userRepository.findByEmail("restaurant@test.com").get();
    // assertEquals(Role.RESTAURANT_ADMIN, savedUser.getRole());
    // assertEquals(999L, savedUser.getRestaurantId());
    // }

    // @Test
    // void registerDriver_endToEnd_createsUserWithDriverId() throws Exception {
    // RegisterRequestDto req = new RegisterRequestDto(
    // "driver@test.com",
    // "password123",
    // "Test",
    // "Driver",
    // "",
    // "",
    // "");

    // when(deliveryServiceClient.createDriver()).thenReturn(Map.of("driverId",
    // 888L));
    // when(jwtService.generateToken(any(), any())).thenReturn("driver-token");

    // mockMvc.perform(post("/api/users/register/driver")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(objectMapper.writeValueAsString(req)))
    // .andExpect(status().isOk())
    // .andExpect(jsonPath("$.token").value("driver-token"));

    // // Verify user was created with driver ID
    // assertTrue(userRepository.findByEmail("driver@test.com").isPresent());
    // User savedUser = userRepository.findByEmail("driver@test.com").get();
    // assertEquals(Role.DELIVERY_DRIVER, savedUser.getRole());
    // assertEquals(888L, savedUser.getDriverId());
    // }

    // @Test
    // void registerCustomer_withDuplicateEmail_returnsBadRequest() throws Exception
    // {
    // // Create initial user
    // User existingUser = new User();
    // existingUser.setEmail("duplicate@test.com");
    // existingUser.setPassword("encoded");
    // existingUser.setFirstName("Existing");
    // existingUser.setLastName("User");
    // existingUser.setRole(Role.CUSTOMER);
    // userRepository.save(existingUser);

    // RegisterRequestDto req = new RegisterRequestDto(
    // "duplicate@test.com",
    // "password123",
    // "New",
    // "User",
    // "",
    // "",
    // "");

    // mockMvc.perform(post("/api/users/register/customer")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(objectMapper.writeValueAsString(req)))
    // .andExpect(status().isConflict());
    // }

    // @Test
    // @WithMockUser(username = "2")
    // void getUserProfile_endToEnd_returnsUserData() throws Exception {
    // // Create a user
    // User user = new User();
    // user.setEmail("profile@test.com");
    // user.setPassword("encoded");
    // user.setFirstName("Profile");
    // user.setLastName("User");
    // user.setRole(Role.CUSTOMER);
    // User saved = userRepository.save(user);

    // mockMvc.perform(get("/api/users/profile")
    // .principal(() -> saved.getId().toString()))
    // .andExpect(status().isOk())
    // .andExpect(jsonPath("$.email").value("profile@test.com"))
    // .andExpect(jsonPath("$.firstName").value("Profile"));
    // }

    // @Test
    // void login_withValidCredentials_returnsToken() {
    // // Create a user first
    // User user = new User();
    // user.setEmail("login@test.com");
    // user.setPassword("$2a$10$dummyEncodedPassword"); // BCrypt encoded
    // user.setFirstName("Login");
    // user.setLastName("User");
    // user.setRole(Role.CUSTOMER);
    // userRepository.save(user);

    // when(jwtService.generateToken(any(), any())).thenReturn("login-token");

    // // Verify user was created in database
    // assertTrue(userRepository.findByEmail("login@test.com").isPresent());
    // User savedUser = userRepository.findByEmail("login@test.com").get();
    // assertEquals("Login", savedUser.getFirstName());
    // assertEquals(Role.CUSTOMER, savedUser.getRole());

    // // Note: Full login test would require proper authentication manager setup
    // // This test verifies user creation and database persistence
    // }
}
