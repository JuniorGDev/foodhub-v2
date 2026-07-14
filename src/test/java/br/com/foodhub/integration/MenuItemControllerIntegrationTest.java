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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Menu Item Controller Integration Tests")
class MenuItemControllerIntegrationTest {

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
    private UUID existingMenuItemId;

    @BeforeEach
    void setUp() {
        bellaNapoliRestaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        sushiHouseRestaurantId = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
        existingMenuItemId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    }

    private MenuItemRequest createMenuItemRequest(String name, String description, BigDecimal price, 
                                                  boolean availableOnlyInRestaurant, String imagePath, UUID restaurantId) {
        return new MenuItemRequest(name, description, price, availableOnlyInRestaurant, imagePath, restaurantId);
    }

    @Nested
    @DisplayName("GET /api/v1/menu-items")
    class FindAllMenuItemsTests {

        @Test
        @DisplayName("Should return 200 with list of menu items")
        void shouldReturn200WithListOfMenuItems() throws Exception {
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
        @DisplayName("Should return 200 when menu item exists")
        void shouldReturn200WhenMenuItemExists() throws Exception {
            // Given
            UUID menuItemId = existingMenuItemId;

            // When & Then
            mockMvc.perform(get("/api/v1/menu-items/{id}", menuItemId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(menuItemId.toString()))
                    .andExpect(jsonPath("$.name").exists());
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

        @Test
        @DisplayName("Should return 400 when UUID is invalid")
        void shouldReturn400WhenUUIDIsInvalid() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/menu-items/{id}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/menu-items")
    class CreateMenuItemTests {

        @Test
        @DisplayName("Should return 201 and persist menu item")
        void shouldReturn201AndPersistMenuItem() throws Exception {
            // Given
            MenuItemRequest request = createMenuItemRequest(
                    "Spaghetti Carbonara",
                    "Classic Italian pasta with bacon and eggs",
                    new BigDecimal("45.90"),
                    false,
                    "/images/spaghetti-carbonara.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            String response = mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(request.name()))
                    .andExpect(jsonPath("$.description").value(request.description()))
                    .andExpect(jsonPath("$.price").value(45.9))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Verify persistence
            String menuItemId = objectMapper.readTree(response).get("id").asText();
            assertThat(menuItemRepository.findById(UUID.fromString(menuItemId))).isPresent();
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            MenuItemRequest request = createMenuItemRequest(
                    "",
                    "Description",
                    new BigDecimal("45.90"),
                    false,
                    "/images/test.png",
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
            MenuItemRequest request = createMenuItemRequest(
                    "ab",
                    "Description",
                    new BigDecimal("45.90"),
                    false,
                    "/images/test.png",
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
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "",
                    new BigDecimal("45.90"),
                    false,
                    "/images/test.png",
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
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    null,
                    false,
                    "/images/test.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when price is zero or negative")
        void shouldReturn400WhenPriceIsZeroOrNegative() throws Exception {
            // Given
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    BigDecimal.ZERO,
                    false,
                    "/images/test.png",
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
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    new BigDecimal("45.90"),
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
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    new BigDecimal("45.90"),
                    false,
                    "/images/test.png",
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
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    new BigDecimal("45.90"),
                    false,
                    "/images/test.png",
                    nonExistentRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 422 when price is invalid")
        void shouldReturn422WhenPriceIsInvalid() throws Exception {
            // Given
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    new BigDecimal("-10.00"),
                    false,
                    "/images/test.png",
                    bellaNapoliRestaurantId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/menu-items/{id}")
    class UpdateMenuItemTests {

        @Test
        @DisplayName("Should return 200 and update menu item")
        void shouldReturn200AndUpdateMenuItem() throws Exception {
            // Given
            UUID menuItemId = existingMenuItemId;
            MenuItemRequest request = createMenuItemRequest(
                    "Updated Pizza Margherita",
                    "Updated description",
                    new BigDecimal("65.90"),
                    true,
                    "/images/updated-pizza-margherita.png",
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
                    .andExpect(jsonPath("$.price").value(65.9));

            // Verify persistence
            assertThat(menuItemRepository.findById(menuItemId)).isPresent();
            assertThat(menuItemRepository.findById(menuItemId).get().getName()).isEqualTo(request.name());
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UUID menuItemId = existingMenuItemId;
            MenuItemRequest request = createMenuItemRequest(
                    "",
                    "Description",
                    new BigDecimal("45.90"),
                    false,
                    "/images/test.png",
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
            UUID menuItemId = existingMenuItemId;
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "",
                    new BigDecimal("45.90"),
                    false,
                    "/images/test.png",
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
            UUID menuItemId = existingMenuItemId;
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    null,
                    false,
                    "/images/test.png",
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
            UUID menuItemId = existingMenuItemId;
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    new BigDecimal("45.90"),
                    false,
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
            UUID menuItemId = existingMenuItemId;
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    new BigDecimal("45.90"),
                    false,
                    "/images/test.png",
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
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    new BigDecimal("45.90"),
                    false,
                    "/images/test.png",
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
            UUID menuItemId = existingMenuItemId;
            UUID nonExistentRestaurantId = UUID.randomUUID();
            MenuItemRequest request = createMenuItemRequest(
                    "Test Item",
                    "Description",
                    new BigDecimal("45.90"),
                    false,
                    "/images/test.png",
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
        @DisplayName("Should return 204 and delete menu item")
        void shouldReturn204AndDeleteMenuItem() throws Exception {
            // Given - First create a menu item
            MenuItemRequest createRequest = createMenuItemRequest(
                    "To Delete",
                    "Description",
                    new BigDecimal("45.90"),
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

            // Verify deletion
            assertThat(menuItemRepository.findById(UUID.fromString(menuItemId))).isEmpty();
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
