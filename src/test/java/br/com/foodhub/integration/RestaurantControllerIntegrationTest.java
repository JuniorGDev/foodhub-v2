package br.com.foodhub.integration;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Restaurant Controller Integration Tests")
class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID brazilianKitchenTypeId;
    private UUID italianKitchenTypeId;
    private UUID japaneseKitchenTypeId;
    private UUID ownerUserId;
    private UUID adminUserId;
    private UUID customerUserId;
    private UUID existingRestaurantId;

    @BeforeEach
    void setUp() {
        brazilianKitchenTypeId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        italianKitchenTypeId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        japaneseKitchenTypeId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        ownerUserId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        adminUserId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        customerUserId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        existingRestaurantId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    }

    private RestaurantRequest createRestaurantRequest(String name, UUID kitchenTypeId, String address, 
                                                       LocalTime openingTime, LocalTime closingTime, UUID ownerId) {
        return new RestaurantRequest(name, kitchenTypeId, address, openingTime, closingTime, ownerId);
    }

    @Nested
    @DisplayName("GET /api/v1/restaurants")
    class FindAllRestaurantsTests {

        @Test
        @DisplayName("Should return 200 with list of restaurants")
        void shouldReturn200WithListOfRestaurants() throws Exception {
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
        @DisplayName("Should return 200 when restaurant exists")
        void shouldReturn200WhenRestaurantExists() throws Exception {
            // Given
            UUID restaurantId = existingRestaurantId;

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

        @Test
        @DisplayName("Should return 400 when UUID is invalid")
        void shouldReturn400WhenUUIDIsInvalid() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/restaurants/{id}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/restaurants")
    class CreateRestaurantTests {

        @Test
        @DisplayName("Should return 201 and persist restaurant")
        void shouldReturn201AndPersistRestaurant() throws Exception {
            // Given
            RestaurantRequest request = createRestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Nova, 100",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    ownerUserId
            );

            // When & Then
            String response = mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(request.name()))
                    .andExpect(jsonPath("$.address").value(request.address()))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Verify persistence
            String restaurantId = objectMapper.readTree(response).get("id").asText();
            assertThat(restaurantRepository.findById(UUID.fromString(restaurantId))).isPresent();
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            RestaurantRequest request = createRestaurantRequest(
                    "",
                    brazilianKitchenTypeId,
                    "Rua Nova, 100",
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
            RestaurantRequest request = createRestaurantRequest(
                    "ab",
                    brazilianKitchenTypeId,
                    "Rua Nova, 100",
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
            RestaurantRequest request = createRestaurantRequest(
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
        @DisplayName("Should return 400 when kitchenTypeId is null")
        void shouldReturn400WhenKitchenTypeIdIsNull() throws Exception {
            // Given
            RestaurantRequest request = createRestaurantRequest(
                    "New Restaurant",
                    null,
                    "Rua Nova, 100",
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
            RestaurantRequest request = createRestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Nova, 100",
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
            RestaurantRequest request = createRestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Nova, 100",
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
        @DisplayName("Should return 400 when ownerId is null")
        void shouldReturn400WhenOwnerIdIsNull() throws Exception {
            // Given
            RestaurantRequest request = createRestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Nova, 100",
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
            RestaurantRequest request = createRestaurantRequest(
                    "New Restaurant",
                    nonExistentKitchenTypeId,
                    "Rua Nova, 100",
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
            RestaurantRequest request = createRestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Nova, 100",
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
        @DisplayName("Should return 422 when user is not allowed to own restaurant")
        void shouldReturn422WhenUserIsNotAllowedToOwnRestaurant() throws Exception {
            // Given
            RestaurantRequest request = createRestaurantRequest(
                    "New Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Nova, 100",
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
    @DisplayName("PUT /api/v1/restaurants/{id}")
    class UpdateRestaurantTests {

        @Test
        @DisplayName("Should return 200 and update restaurant")
        void shouldReturn200AndUpdateRestaurant() throws Exception {
            // Given
            UUID restaurantId = existingRestaurantId;
            RestaurantRequest request = createRestaurantRequest(
                    "Updated Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Atualizada, 200",
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

            // Verify persistence
            assertThat(restaurantRepository.findById(restaurantId)).isPresent();
            assertThat(restaurantRepository.findById(restaurantId).get().getName()).isEqualTo(request.name());
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UUID restaurantId = existingRestaurantId;
            RestaurantRequest request = createRestaurantRequest(
                    "",
                    brazilianKitchenTypeId,
                    "Rua Atualizada, 200",
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
            UUID restaurantId = existingRestaurantId;
            RestaurantRequest request = createRestaurantRequest(
                    "Updated Restaurant",
                    brazilianKitchenTypeId,
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
        @DisplayName("Should return 400 when kitchenTypeId is null")
        void shouldReturn400WhenKitchenTypeIdIsNull() throws Exception {
            // Given
            UUID restaurantId = existingRestaurantId;
            RestaurantRequest request = createRestaurantRequest(
                    "Updated Restaurant",
                    null,
                    "Rua Atualizada, 200",
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
            UUID restaurantId = existingRestaurantId;
            RestaurantRequest request = createRestaurantRequest(
                    "Updated Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Atualizada, 200",
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
            UUID restaurantId = existingRestaurantId;
            RestaurantRequest request = createRestaurantRequest(
                    "Updated Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Atualizada, 200",
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
        @DisplayName("Should return 400 when ownerId is null")
        void shouldReturn400WhenOwnerIdIsNull() throws Exception {
            // Given
            UUID restaurantId = existingRestaurantId;
            RestaurantRequest request = createRestaurantRequest(
                    "Updated Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Atualizada, 200",
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
            RestaurantRequest request = createRestaurantRequest(
                    "Updated Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Atualizada, 200",
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
            UUID restaurantId = existingRestaurantId;
            UUID nonExistentKitchenTypeId = UUID.randomUUID();
            RestaurantRequest request = createRestaurantRequest(
                    "Updated Restaurant",
                    nonExistentKitchenTypeId,
                    "Rua Atualizada, 200",
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
            UUID restaurantId = existingRestaurantId;
            UUID nonExistentOwnerId = UUID.randomUUID();
            RestaurantRequest request = createRestaurantRequest(
                    "Updated Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Atualizada, 200",
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
        @DisplayName("Should return 422 when user is not allowed to own restaurant")
        void shouldReturn422WhenUserIsNotAllowedToOwnRestaurant() throws Exception {
            // Given
            UUID restaurantId = existingRestaurantId;
            RestaurantRequest request = createRestaurantRequest(
                    "Updated Restaurant",
                    brazilianKitchenTypeId,
                    "Rua Atualizada, 200",
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
        @DisplayName("Should return 204 and delete restaurant")
        void shouldReturn204AndDeleteRestaurant() throws Exception {
            // Given - First create a restaurant
            RestaurantRequest createRequest = createRestaurantRequest(
                    "To Delete",
                    brazilianKitchenTypeId,
                    "Rua Delete, 300",
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

            // Verify deletion
            assertThat(restaurantRepository.findById(UUID.fromString(restaurantId))).isEmpty();
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
