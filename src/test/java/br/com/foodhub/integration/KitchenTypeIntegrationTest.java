package br.com.foodhub.integration;

import br.com.foodhub.infrastructure.repository.KitchenTypeRepository;
import br.com.foodhub.presentation.request.KitchenTypeRequest;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("KitchenType Integration Tests")
class KitchenTypeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KitchenTypeRepository kitchenTypeRepository;

    private UUID brazilianKitchenTypeId;
    private UUID italianKitchenTypeId;
    private UUID japaneseKitchenTypeId;

    @BeforeEach
    void setUp() {
        brazilianKitchenTypeId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        italianKitchenTypeId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        japaneseKitchenTypeId = UUID.fromString("66666666-6666-6666-6666-666666666666");
    }

    @Nested
    @DisplayName("POST /api/v1/kitchen-types")
    class CreateKitchenTypeTests {

        @Test
        @DisplayName("Should create kitchen type with status 201")
        void shouldCreateKitchenTypeWithStatus201() throws Exception {
            // Given
            KitchenTypeRequest request = new KitchenTypeRequest("MEXICAN");

            // When & Then
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("mexican"));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            KitchenTypeRequest request = new KitchenTypeRequest("");

            // When & Then
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void shouldReturn400WhenNameIsTooShort() throws Exception {
            // Given
            KitchenTypeRequest request = new KitchenTypeRequest("ab");

            // When & Then
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when kitchen type already exists")
        void shouldReturn409WhenKitchenTypeAlreadyExists() throws Exception {
            // Given - First create a new kitchen type with a unique name
            String uniqueName = "TEMP_" + System.currentTimeMillis();
            KitchenTypeRequest createRequest = new KitchenTypeRequest(uniqueName);
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)));

            KitchenTypeRequest request = new KitchenTypeRequest(uniqueName);

            // When & Then
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/kitchen-types")
    class FindAllKitchenTypesTests {

        @Test
        @DisplayName("Should return all kitchen types with status 200")
        void shouldReturnAllKitchenTypesWithStatus200() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/kitchen-types"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/kitchen-types/{id}")
    class FindKitchenTypeByIdTests {

        @Test
        @DisplayName("Should return kitchen type by ID with status 200")
        void shouldReturnKitchenTypeByIdWithStatus200() throws Exception {
            // Given
            // When & Then
            mockMvc.perform(get("/api/v1/kitchen-types/{id}", brazilianKitchenTypeId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(brazilianKitchenTypeId.toString()))
                    .andExpect(jsonPath("$.name").value("BRAZILIAN"));
        }

        @Test
        @DisplayName("Should return 404 when kitchen type not found")
        void shouldReturn404WhenKitchenTypeNotFound() throws Exception {
            // Given
            UUID nonExistentKitchenTypeId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(get("/api/v1/kitchen-types/{id}", nonExistentKitchenTypeId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/kitchen-types/{id}")
    class UpdateKitchenTypeTests {

        @Test
        @DisplayName("Should update kitchen type with status 200")
        void shouldUpdateKitchenTypeWithStatus200() throws Exception {
            // Given - First create a new kitchen type
            KitchenTypeRequest createRequest = new KitchenTypeRequest("TEMP");
            String response = mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String kitchenTypeId = objectMapper.readTree(response).get("id").asText();

            KitchenTypeRequest updateRequest = new KitchenTypeRequest("UPDATED");

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", kitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("updated"));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            KitchenTypeRequest request = new KitchenTypeRequest("");

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", brazilianKitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void shouldReturn400WhenNameIsTooShort() throws Exception {
            // Given
            KitchenTypeRequest request = new KitchenTypeRequest("ab");

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", brazilianKitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when kitchen type not found")
        void shouldReturn404WhenKitchenTypeNotFound() throws Exception {
            // Given
            UUID nonExistentKitchenTypeId = UUID.randomUUID();
            KitchenTypeRequest request = new KitchenTypeRequest("UPDATED");

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", nonExistentKitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when kitchen type already exists")
        void shouldReturn409WhenKitchenTypeAlreadyExists() throws Exception {
            // Given
            KitchenTypeRequest request = new KitchenTypeRequest("ITALIAN");

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", brazilianKitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/kitchen-types/{id}")
    class DeleteKitchenTypeTests {

        @Test
        @DisplayName("Should delete kitchen type with status 204")
        void shouldDeleteKitchenTypeWithStatus204() throws Exception {
            // Given - First create a new kitchen type
            KitchenTypeRequest createRequest = new KitchenTypeRequest("TEMP");
            String response = mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String kitchenTypeId = objectMapper.readTree(response).get("id").asText();

            // When & Then
            mockMvc.perform(delete("/api/v1/kitchen-types/{id}", kitchenTypeId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when kitchen type not found")
        void shouldReturn404WhenKitchenTypeNotFound() throws Exception {
            // Given
            UUID nonExistentKitchenTypeId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/api/v1/kitchen-types/{id}", nonExistentKitchenTypeId))
                    .andExpect(status().isNotFound());
        }
    }
}
