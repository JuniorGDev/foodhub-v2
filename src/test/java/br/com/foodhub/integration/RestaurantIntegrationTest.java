package br.com.foodhub.integration;

import br.com.foodhub.infrastructure.repository.KitchenTypeRepository;
import br.com.foodhub.infrastructure.repository.RestaurantRepository;
import br.com.foodhub.infrastructure.repository.UserRepository;
import br.com.foodhub.presentation.request.RestaurantRequest;
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

import java.time.LocalTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Restaurant Integration Tests")
class RestaurantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private KitchenTypeRepository kitchenTypeRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID brazilianKitchenTypeId;
    private UUID italianKitchenTypeId;
    private UUID japaneseKitchenTypeId;

    private UUID adminUserId;
    private UUID ownerUserId;
    private UUID customerUserId;

    @BeforeEach
    void setUp() {
        brazilianKitchenTypeId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        italianKitchenTypeId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        japaneseKitchenTypeId = UUID.fromString("66666666-6666-6666-6666-666666666666");

        adminUserId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        ownerUserId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        customerUserId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    }

    @Nested
    @DisplayName("POST /api/v1/restaurants")
    class CreateRestaurantTests {

        @Test
        @DisplayName("Should create restaurant with status 201")
        void shouldCreateRestaurantWithStatus201() throws Exception {
            // Given
            RestaurantRequest request = new RestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "123 New St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(request.name()))
                    .andExpect(jsonPath("$.address").value(request.address()));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            RestaurantRequest request = new RestaurantRequest(
                    "",
                    brazilianKitchenTypeId,
                    "123 New St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void shouldReturn400WhenNameIsTooShort() throws Exception {
            // Given
            RestaurantRequest request = new RestaurantRequest(
                    "ab",
                    brazilianKitchenTypeId,
                    "123 New St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when address is blank")
        void shouldReturn400WhenAddressIsBlank() throws Exception {
            // Given
            RestaurantRequest request = new RestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when openingTime is null")
        void shouldReturn400WhenOpeningTimeIsNull() throws Exception {
            // Given
            RestaurantRequest request = new RestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "123 New St",
                    null,
                    LocalTime.of(22, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when closingTime is null")
        void shouldReturn400WhenClosingTimeIsNull() throws Exception {
            // Given
            RestaurantRequest request = new RestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "123 New St",
                    LocalTime.of(10, 0),
                    null,
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when kitchenTypeId is null")
        void shouldReturn400WhenKitchenTypeIdIsNull() throws Exception {
            // Given
            RestaurantRequest request = new RestaurantRequest(
                    "New Restaurant",
                    null,
                    "123 New St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when ownerId is null")
        void shouldReturn400WhenOwnerIdIsNull() throws Exception {
            // Given
            RestaurantRequest request = new RestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "123 New St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    null
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when kitchen type not found")
        void shouldReturn404WhenKitchenTypeNotFound() throws Exception {
            // Given
            UUID nonExistentKitchenTypeId = UUID.randomUUID();
            RestaurantRequest request = new RestaurantRequest(
                    "New Restaurant",
                    nonExistentKitchenTypeId,
                    "123 New St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when owner not found")
        void shouldReturn404WhenOwnerNotFound() throws Exception {
            // Given
            UUID nonExistentOwnerId = UUID.randomUUID();
            RestaurantRequest request = new RestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "123 New St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    nonExistentOwnerId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 422 when owner is not OWNER type")
        void shouldReturn422WhenOwnerIsNotOwnerType() throws Exception {
            // Given
            RestaurantRequest request = new RestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "123 New St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    customerUserId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }

    }

    @Nested
    @DisplayName("GET /api/v1/restaurants")
    class FindAllRestaurantsTests {

        @Test
        @DisplayName("Should return all restaurants with status 200")
        void shouldReturnAllRestaurantsWithStatus200() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/restaurants"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/restaurants/{id}")
    class FindRestaurantByIdTests {

        @Test
        @DisplayName("Should return restaurant by ID with status 200")
        void shouldReturnRestaurantByIdWithStatus200() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

            // When & Then
            mockMvc.perform(get("/api/v1/restaurants/{id}", restaurantId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                    .andExpect(jsonPath("$.name").exists());
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            // Given
            UUID nonExistentRestaurantId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(get("/api/v1/restaurants/{id}", nonExistentRestaurantId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/restaurants/{id}")
    class UpdateRestaurantTests {

        @Test
        @DisplayName("Should update restaurant with status 200")
        void shouldUpdateRestaurantWithStatus200() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
            RestaurantRequest request = new RestaurantRequest(
                    "Updated Restaurant",
                    italianKitchenTypeId,
                    "456 Updated St",
                    LocalTime.of(11, 0),
                    LocalTime.of(23, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(request.name()))
                    .andExpect(jsonPath("$.address").value(request.address()));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
            RestaurantRequest request = new RestaurantRequest(
                    "",
                    italianKitchenTypeId,
                    "456 Updated St",
                    LocalTime.of(11, 0),
                    LocalTime.of(23, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when address is blank")
        void shouldReturn400WhenAddressIsBlank() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
            RestaurantRequest request = new RestaurantRequest(
                    "Updated Restaurant",
                    italianKitchenTypeId,
                    "",
                    LocalTime.of(11, 0),
                    LocalTime.of(23, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when openingTime is null")
        void shouldReturn400WhenOpeningTimeIsNull() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
            RestaurantRequest request = new RestaurantRequest(
                    "Updated Restaurant",
                    italianKitchenTypeId,
                    "456 Updated St",
                    null,
                    LocalTime.of(23, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when closingTime is null")
        void shouldReturn400WhenClosingTimeIsNull() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
            RestaurantRequest request = new RestaurantRequest(
                    "Updated Restaurant",
                    italianKitchenTypeId,
                    "456 Updated St",
                    LocalTime.of(11, 0),
                    null,
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when kitchenTypeId is null")
        void shouldReturn400WhenKitchenTypeIdIsNull() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
            RestaurantRequest request = new RestaurantRequest(
                    "Updated Restaurant",
                    null,
                    "456 Updated St",
                    LocalTime.of(11, 0),
                    LocalTime.of(23, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when ownerId is null")
        void shouldReturn400WhenOwnerIdIsNull() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
            RestaurantRequest request = new RestaurantRequest(
                    "Updated Restaurant",
                    italianKitchenTypeId,
                    "456 Updated St",
                    LocalTime.of(11, 0),
                    LocalTime.of(23, 0),
                    null
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            // Given
            UUID nonExistentRestaurantId = UUID.randomUUID();
            RestaurantRequest request = new RestaurantRequest(
                    "Updated Restaurant",
                    italianKitchenTypeId,
                    "456 Updated St",
                    LocalTime.of(11, 0),
                    LocalTime.of(23, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", nonExistentRestaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when kitchen type not found")
        void shouldReturn404WhenKitchenTypeNotFound() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
            UUID nonExistentKitchenTypeId = UUID.randomUUID();
            RestaurantRequest request = new RestaurantRequest(
                    "Updated Restaurant",
                    nonExistentKitchenTypeId,
                    "456 Updated St",
                    LocalTime.of(11, 0),
                    LocalTime.of(23, 0),
                    ownerUserId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when owner not found")
        void shouldReturn404WhenOwnerNotFound() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
            UUID nonExistentOwnerId = UUID.randomUUID();
            RestaurantRequest request = new RestaurantRequest(
                    "Updated Restaurant",
                    italianKitchenTypeId,
                    "456 Updated St",
                    LocalTime.of(11, 0),
                    LocalTime.of(23, 0),
                    nonExistentOwnerId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 422 when owner is not OWNER type")
        void shouldReturn422WhenOwnerIsNotOwnerType() throws Exception {
            // Given
            UUID restaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
            RestaurantRequest request = new RestaurantRequest(
                    "Updated Restaurant",
                    italianKitchenTypeId,
                    "456 Updated St",
                    LocalTime.of(11, 0),
                    LocalTime.of(23, 0),
                    customerUserId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity());
        }

    }

    @Nested
    @DisplayName("DELETE /api/v1/restaurants/{id}")
    class DeleteRestaurantTests {

        @Test
        @DisplayName("Should delete restaurant with status 204")
        void shouldDeleteRestaurantWithStatus204() throws Exception {
            // Given - First create a new restaurant
            RestaurantRequest createRequest = new RestaurantRequest(
                    "To Delete",
                    brazilianKitchenTypeId,
                    "123 Delete St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    ownerUserId
            );

            String response = mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String restaurantId = objectMapper.readTree(response).get("id").asText();

            // When & Then
            mockMvc.perform(delete("/api/v1/restaurants/{id}", restaurantId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            // Given
            UUID nonExistentRestaurantId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/api/v1/restaurants/{id}", nonExistentRestaurantId))
                    .andExpect(status().isNotFound());
        }
    }
}
