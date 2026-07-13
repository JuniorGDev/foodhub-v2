package br.com.foodhub.integration;

import br.com.foodhub.infrastructure.repository.MenuItemRepository;
import br.com.foodhub.infrastructure.repository.RestaurantRepository;
import br.com.foodhub.presentation.request.MenuItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("MenuItem Integration Tests")
class MenuItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private UUID bellaNapoliRestaurantId;
    private UUID sushiHouseRestaurantId;

    @BeforeEach
    void setUp() {
        bellaNapoliRestaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        sushiHouseRestaurantId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
    }

    @Nested
    @DisplayName("POST /api/v1/menu-items")
    class CreateMenuItemTests {

        @Test
        @DisplayName("Should create menu item with status 201")
        void shouldCreateMenuItemWithStatus201() throws Exception {
            // Given
            MenuItemRequest request = new MenuItemRequest(
                    "New Item",
                    "Delicious new item",
                    new BigDecimal("29.90"),
                    false,
                    "/images/new-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(request.name()))
                    .andExpect(jsonPath("$.description").value(request.description()))
                    .andExpect(jsonPath("$.price").value(29.9))
                    .andExpect(jsonPath("$.availableOnlyInRestaurant").value(request.availableOnlyInRestaurant()))
                    .andExpect(jsonPath("$.imagePath").value(request.imagePath()));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            MenuItemRequest request = new MenuItemRequest(
                    "",
                    "Delicious new item",
                    new BigDecimal("29.90"),
                    false,
                    "/images/new-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void shouldReturn400WhenNameIsTooShort() throws Exception {
            // Given
            MenuItemRequest request = new MenuItemRequest(
                    "ab",
                    "Delicious new item",
                    new BigDecimal("29.90"),
                    false,
                    "/images/new-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when description is blank")
        void shouldReturn400WhenDescriptionIsBlank() throws Exception {
            // Given
            MenuItemRequest request = new MenuItemRequest(
                    "New Item",
                    "",
                    new BigDecimal("29.90"),
                    false,
                    "/images/new-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when price is null")
        void shouldReturn400WhenPriceIsNull() throws Exception {
            // Given
            MenuItemRequest request = new MenuItemRequest(
                    "New Item",
                    "Delicious new item",
                    null,
                    false,
                    "/images/new-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when price is zero")
        void shouldReturn400WhenPriceIsZero() throws Exception {
            // Given
            MenuItemRequest request = new MenuItemRequest(
                    "New Item",
                    "Delicious new item",
                    BigDecimal.ZERO,
                    false,
                    "/images/new-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when price is negative")
        void shouldReturn400WhenPriceIsNegative() throws Exception {
            // Given
            MenuItemRequest request = new MenuItemRequest(
                    "New Item",
                    "Delicious new item",
                    new BigDecimal("-10.00"),
                    false,
                    "/images/new-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when imagePath is blank")
        void shouldReturn400WhenImagePathIsBlank() throws Exception {
            // Given
            MenuItemRequest request = new MenuItemRequest(
                    "New Item",
                    "Delicious new item",
                    new BigDecimal("29.90"),
                    false,
                    "",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when restaurantId is null")
        void shouldReturn400WhenRestaurantIdIsNull() throws Exception {
            // Given
            MenuItemRequest request = new MenuItemRequest(
                    "New Item",
                    "Delicious new item",
                    new BigDecimal("29.90"),
                    false,
                    "/images/new-item.png",
                    null
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            // Given
            UUID nonExistentRestaurantId = UUID.randomUUID();
            MenuItemRequest request = new MenuItemRequest(
                    "New Item",
                    "Delicious new item",
                    new BigDecimal("29.90"),
                    false,
                    "/images/new-item.png",
                    nonExistentRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    @DisplayName("GET /api/v1/menu-items")
    class FindAllMenuItemsTests {

        @Test
        @DisplayName("Should return all menu items with status 200")
        void shouldReturnAllMenuItemsWithStatus200() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/menu-items"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/menu-items/{id}")
    class FindMenuItemByIdTests {

        @Test
        @DisplayName("Should return menu item by ID with status 200")
        void shouldReturnMenuItemByIdWithStatus200() throws Exception {
            // Given
            UUID menuItemId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

            // When & Then
            mockMvc.perform(get("/api/v1/menu-items/{id}", menuItemId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(menuItemId.toString()))
                    .andExpect(jsonPath("$.name").exists())
                    .andExpect(jsonPath("$.description").exists())
                    .andExpect(jsonPath("$.price").exists());
        }

        @Test
        @DisplayName("Should return 404 when menu item not found")
        void shouldReturn404WhenMenuItemNotFound() throws Exception {
            // Given
            UUID nonExistentMenuItemId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(get("/api/v1/menu-items/{id}", nonExistentMenuItemId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/menu-items/{id}")
    class UpdateMenuItemTests {

        @Test
        @DisplayName("Should update menu item with status 200")
        void shouldUpdateMenuItemWithStatus200() throws Exception {
            // Given
            UUID menuItemId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
            MenuItemRequest request = new MenuItemRequest(
                    "Updated Item",
                    "Updated description",
                    new BigDecimal("39.90"),
                    true,
                    "/images/updated-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(request.name()))
                    .andExpect(jsonPath("$.description").value(request.description()))
                    .andExpect(jsonPath("$.price").value(39.9))
                    .andExpect(jsonPath("$.availableOnlyInRestaurant").value(request.availableOnlyInRestaurant()))
                    .andExpect(jsonPath("$.imagePath").value(request.imagePath()));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UUID menuItemId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
            MenuItemRequest request = new MenuItemRequest(
                    "",
                    "Updated description",
                    new BigDecimal("39.90"),
                    true,
                    "/images/updated-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when description is blank")
        void shouldReturn400WhenDescriptionIsBlank() throws Exception {
            // Given
            UUID menuItemId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
            MenuItemRequest request = new MenuItemRequest(
                    "Updated Item",
                    "",
                    new BigDecimal("39.90"),
                    true,
                    "/images/updated-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when price is null")
        void shouldReturn400WhenPriceIsNull() throws Exception {
            // Given
            UUID menuItemId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
            MenuItemRequest request = new MenuItemRequest(
                    "Updated Item",
                    "Updated description",
                    null,
                    true,
                    "/images/updated-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when imagePath is blank")
        void shouldReturn400WhenImagePathIsBlank() throws Exception {
            // Given
            UUID menuItemId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
            MenuItemRequest request = new MenuItemRequest(
                    "Updated Item",
                    "Updated description",
                    new BigDecimal("39.90"),
                    true,
                    "",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when restaurantId is null")
        void shouldReturn400WhenRestaurantIdIsNull() throws Exception {
            // Given
            UUID menuItemId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
            MenuItemRequest request = new MenuItemRequest(
                    "Updated Item",
                    "Updated description",
                    new BigDecimal("39.90"),
                    true,
                    "/images/updated-item.png",
                    null
            );

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when menu item not found")
        void shouldReturn404WhenMenuItemNotFound() throws Exception {
            // Given
            UUID nonExistentMenuItemId = UUID.randomUUID();
            MenuItemRequest request = new MenuItemRequest(
                    "Updated Item",
                    "Updated description",
                    new BigDecimal("39.90"),
                    true,
                    "/images/updated-item.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", nonExistentMenuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            // Given
            UUID menuItemId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
            UUID nonExistentRestaurantId = UUID.randomUUID();
            MenuItemRequest request = new MenuItemRequest(
                    "Updated Item",
                    "Updated description",
                    new BigDecimal("39.90"),
                    true,
                    "/images/updated-item.png",
                    nonExistentRestaurantId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    @DisplayName("DELETE /api/v1/menu-items/{id}")
    class DeleteMenuItemTests {

        @Test
        @DisplayName("Should delete menu item with status 204")
        void shouldDeleteMenuItemWithStatus204() throws Exception {
            // Given - First create a new menu item
            MenuItemRequest createRequest = new MenuItemRequest(
                    "To Delete",
                    "Item to be deleted",
                    new BigDecimal("19.90"),
                    false,
                    "/images/to-delete.png",
                    bellaNapoliRestaurantId
            );

            String response = mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String menuItemId = objectMapper.readTree(response).get("id").asText();

            // When & Then
            mockMvc.perform(delete("/api/v1/menu-items/{id}", menuItemId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when menu item not found")
        void shouldReturn404WhenMenuItemNotFound() throws Exception {
            // Given
            UUID nonExistentMenuItemId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/api/v1/menu-items/{id}", nonExistentMenuItemId))
                    .andExpect(status().isNotFound());
        }
    }
}
