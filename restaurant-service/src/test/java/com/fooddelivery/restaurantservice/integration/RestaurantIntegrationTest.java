package com.fooddelivery.restaurantservice.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurantservice.dto.MenuItemRequestDto;
import com.fooddelivery.restaurantservice.model.MenuItem;
import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.repository.MenuItemRepository;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.shared.dto.RestaurantRequestDto;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.cloud.discovery.enabled=false",
        "spring.kafka.bootstrap-servers=localhost:9092"
})
/*
 * Note: Null type safety warnings are common in test code and don't affect
 * test execution.
 */
class RestaurantIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        menuItemRepository.deleteAll();
        restaurantRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "1000", roles = "RESTAURANT_ADMIN")
    void createRestaurant_endToEnd_savesRestaurantToDatabase() throws Exception {
        // Arrange
        RestaurantRequestDto requestDto = new RestaurantRequestDto(
                "Integration Test Restaurant",
                "123 Test Street",
                "Test Cuisine");

        // Act & Assert
        mockMvc.perform(post("/internal/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").exists());

        // Verify it's in the database
        Restaurant foundRestaurant = restaurantRepository.findByOwnerId(1000L).orElse(null);
        assertTrue(foundRestaurant != null);
        assertEquals("Integration Test Restaurant", foundRestaurant.getName());
        assertEquals("123 Test Street", foundRestaurant.getAddress());
        assertEquals("Test Cuisine", foundRestaurant.getCuisineType());
    }

    @Test
    @WithMockUser(username = "2000", roles = "RESTAURANT_ADMIN")
    void createRestaurant_withDuplicateOwner_returnsConflict() throws Exception {
        // Arrange - Create first restaurant
        RestaurantRequestDto firstRequest = new RestaurantRequestDto(
                "First Restaurant",
                "Address 1",
                "Cuisine 1");

        mockMvc.perform(post("/internal/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        // Act & Assert - Try to create second restaurant with same owner
        RestaurantRequestDto secondRequest = new RestaurantRequestDto(
                "Second Restaurant",
                "Address 2",
                "Cuisine 2");

        mockMvc.perform(post("/internal/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "3000", roles = "RESTAURANT_ADMIN")
    void addMenuItem_endToEnd_savesMenuItemToDatabase() throws Exception {
        // Arrange - Create restaurant first
        RestaurantRequestDto restaurantRequest = new RestaurantRequestDto(
                "Test Restaurant for Menu",
                "456 Menu Street",
                "Italian");

        mockMvc.perform(post("/internal/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isOk());

        Restaurant restaurant = restaurantRepository.findByOwnerId(3000L).get();

        MenuItemRequestDto menuItemRequest = new MenuItemRequestDto(
                "Spaghetti Carbonara",
                "Classic Italian pasta",
                new BigDecimal("15.99"));

        // Act & Assert
        mockMvc.perform(post("/api/restaurants/manage/" + restaurant.getId() + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menuItemRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Spaghetti Carbonara"))
                .andExpect(jsonPath("$.description").value("Classic Italian pasta"))
                .andExpect(jsonPath("$.price").value(15.99));

        // Verify it's in the database
        List<MenuItem> menuItemsInDb = menuItemRepository.findByRestaurantId(restaurant.getId());
        assertEquals(1, menuItemsInDb.size());
        assertEquals("Spaghetti Carbonara", menuItemsInDb.get(0).getName());
    }

    @Test
    @WithMockUser(username = "4000", roles = "RESTAURANT_ADMIN")
    void updateMenuItem_endToEnd_updatesMenuItemInDatabase() throws Exception {
        // Arrange - Create restaurant
        RestaurantRequestDto restaurantRequest = new RestaurantRequestDto(
                "Update Test Restaurant",
                "789 Update Street",
                "French");

        mockMvc.perform(post("/internal/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isOk());

        Restaurant restaurant = restaurantRepository.findByOwnerId(4000L).get();

        // Create menu item
        MenuItemRequestDto originalMenuItem = new MenuItemRequestDto(
                "Original Name",
                "Original Description",
                new BigDecimal("10.00"));

        mockMvc.perform(post("/api/restaurants/manage/" + restaurant.getId() + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(originalMenuItem)))
                .andExpect(status().isCreated());

        List<MenuItem> menuItemsInDb = menuItemRepository.findByRestaurantId(restaurant.getId());
        MenuItem createdItem = menuItemsInDb.get(0);

        // Act - Update menu item
        MenuItemRequestDto updatedMenuItem = new MenuItemRequestDto(
                "Updated Name",
                "Updated Description",
                new BigDecimal("12.50"));

        mockMvc.perform(put("/api/restaurants/manage/" + restaurant.getId() + "/menu/" + createdItem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMenuItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.price").value(12.50));

        // Verify it's updated in the database
        MenuItem foundMenuItem = menuItemRepository.findById(createdItem.getId()).get();
        assertEquals("Updated Name", foundMenuItem.getName());
    }

    @Test
    @WithMockUser(username = "5000", roles = "RESTAURANT_ADMIN")
    void deleteMenuItem_endToEnd_removesMenuItemFromDatabase() throws Exception {
        // Arrange - Create restaurant
        RestaurantRequestDto restaurantRequest = new RestaurantRequestDto(
                "Delete Test Restaurant",
                "321 Delete Street",
                "Mexican");

        mockMvc.perform(post("/internal/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isOk());

        Restaurant restaurant = restaurantRepository.findByOwnerId(5000L).get();

        // Create menu item
        MenuItemRequestDto menuItemRequest = new MenuItemRequestDto(
                "Item to Delete",
                "This will be deleted",
                new BigDecimal("8.99"));

        mockMvc.perform(post("/api/restaurants/manage/" + restaurant.getId() + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menuItemRequest)))
                .andExpect(status().isCreated());

        List<MenuItem> menuItemsInDb = menuItemRepository.findByRestaurantId(restaurant.getId()).stream().toList();
        MenuItem createdItem = menuItemsInDb.get(0);
        Long itemId = createdItem.getId();

        // Act - Delete menu item
        mockMvc.perform(delete("/api/restaurants/manage/" + restaurant.getId() + "/menu/" + itemId))
                .andExpect(status().isNoContent());

        // Assert - verify it's removed from the database
        assertFalse(menuItemRepository.findById(itemId).isPresent());
    }

    @Test
    @WithMockUser(username = "6000", roles = "RESTAURANT_ADMIN")
    void getRestaurantById_endToEnd_retrievesRestaurantFromDatabase() throws Exception {
        // Arrange - Create restaurant
        RestaurantRequestDto restaurantRequest = new RestaurantRequestDto(
                "Retrieve Test Restaurant",
                "654 Retrieve Avenue",
                "Japanese");

        mockMvc.perform(post("/internal/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isOk());

        Restaurant createdRestaurant = restaurantRepository.findByOwnerId(6000L).get();

        // Act & Assert - Retrieve restaurant via public endpoint
        mockMvc.perform(get("/api/restaurants/" + createdRestaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdRestaurant.getId()))
                .andExpect(jsonPath("$.restaurantName").value("Retrieve Test Restaurant"))
                .andExpect(jsonPath("$.address").value("654 Retrieve Avenue"))
                .andExpect(jsonPath("$.cuisineType").value("Japanese"));
    }

    @Test
    @WithMockUser(username = "7000", roles = "RESTAURANT_ADMIN")
    void getRestaurantMenu_endToEnd_retrievesMenuFromDatabase() throws Exception {
        // Arrange - Create restaurant
        RestaurantRequestDto restaurantRequest = new RestaurantRequestDto(
                "Menu Test Restaurant",
                "987 Menu Boulevard",
                "Thai");

        mockMvc.perform(post("/internal/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isOk());

        Restaurant restaurant = restaurantRepository.findByOwnerId(7000L).get();

        // Add menu items
        MenuItemRequestDto item1 = new MenuItemRequestDto(
                "Pad Thai",
                "Traditional Thai noodles",
                new BigDecimal("12.99"));

        mockMvc.perform(post("/api/restaurants/manage/" + restaurant.getId() + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item1)))
                .andExpect(status().isCreated());

        MenuItemRequestDto item2 = new MenuItemRequestDto(
                "Green Curry",
                "Spicy Thai curry",
                new BigDecimal("14.99"));

        mockMvc.perform(post("/api/restaurants/manage/" + restaurant.getId() + "/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item2)))
                .andExpect(status().isCreated());

        // Act & Assert - Get restaurant menu via public endpoint
        mockMvc.perform(get("/api/restaurants/" + restaurant.getId() + "/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }
}
