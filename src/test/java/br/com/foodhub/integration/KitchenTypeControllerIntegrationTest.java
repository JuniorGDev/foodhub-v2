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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Kitchen Type Controller Integration Tests")
class KitchenTypeControllerIntegrationTest {

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

    private KitchenTypeRequest createKitchenTypeRequest(String name) {
        return new KitchenTypeRequest(name);
    }

    @Nested
    @DisplayName("GET /api/v1/kitchen-types")
    class FindAllKitchenTypesTests {

        @Test
        @DisplayName("Should return 200 with list of kitchen types")
        void shouldReturn200WithListOfKitchenTypes() throws Exception {
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
        @DisplayName("Should return 200 when kitchen type exists")
        void shouldReturn200WhenKitchenTypeExists() throws Exception {
            // Given
            UUID kitchenTypeId = italianKitchenTypeId;

            // When & Then
            mockMvc.perform(get("/api/v1/kitchen-types/{id}", kitchenTypeId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(kitchenTypeId.toString()))
                    .andExpect(jsonPath("$.name").exists());
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

        @Test
        @DisplayName("Should return 400 when UUID is invalid")
        void shouldReturn400WhenUUIDIsInvalid() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/kitchen-types/{id}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/kitchen-types")
    class CreateKitchenTypeTests {

        @Test
        @DisplayName("Should return 201 and persist kitchen type")
        void shouldReturn201AndPersistKitchenType() throws Exception {
            // Given
            KitchenTypeRequest request = createKitchenTypeRequest("MEXICAN");

            // When & Then
            String response = mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("MEXICAN"))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Verify persistence
            String kitchenTypeId = objectMapper.readTree(response).get("id").asText();
            assertThat(kitchenTypeRepository.findById(UUID.fromString(kitchenTypeId))).isPresent();
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            KitchenTypeRequest request = createKitchenTypeRequest("");

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
            KitchenTypeRequest request = createKitchenTypeRequest("ab");

            // When & Then
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when name already exists")
        void shouldReturn409WhenNameAlreadyExists() throws Exception {
            // Given - First create a kitchen type to test conflict
            KitchenTypeRequest createRequest = createKitchenTypeRequest("FRENCH");
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated());

            // When & Then - Try to create the same name again
            KitchenTypeRequest request = createKitchenTypeRequest("FRENCH");
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/kitchen-types/{id}")
    class UpdateKitchenTypeTests {

        @Test
        @DisplayName("Should return 200 and update kitchen type")
        void shouldReturn200AndUpdateKitchenType() throws Exception {
            // Given
            UUID kitchenTypeId = japaneseKitchenTypeId;
            KitchenTypeRequest request = createKitchenTypeRequest("THAI");

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", kitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("THAI"));

            // Verify persistence
            assertThat(kitchenTypeRepository.findById(kitchenTypeId)).isPresent();
            assertThat(kitchenTypeRepository.findById(kitchenTypeId).get().getName()).isEqualTo("THAI");
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UUID kitchenTypeId = japaneseKitchenTypeId;
            KitchenTypeRequest request = createKitchenTypeRequest("");

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", kitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void shouldReturn400WhenNameIsTooShort() throws Exception {
            // Given
            UUID kitchenTypeId = japaneseKitchenTypeId;
            KitchenTypeRequest request = createKitchenTypeRequest("ab");

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", kitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when kitchen type not found")
        void shouldReturn404WhenKitchenTypeNotFound() throws Exception {
            // Given
            UUID nonExistentKitchenTypeId = UUID.randomUUID();
            KitchenTypeRequest request = createKitchenTypeRequest("MEXICAN");

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", nonExistentKitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when name already exists")
        void shouldReturn409WhenNameAlreadyExists() throws Exception {
            // Given - First create a kitchen type to test conflict
            KitchenTypeRequest createRequest = createKitchenTypeRequest("INDIAN");
            String response = mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            String newKitchenTypeId = objectMapper.readTree(response).get("id").asText();

            // When & Then - Try to update to a name that already exists
            UUID kitchenTypeId = japaneseKitchenTypeId;
            KitchenTypeRequest request = createKitchenTypeRequest("INDIAN");
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", kitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/kitchen-types/{id}")
    class DeleteKitchenTypeTests {

        @Test
        @DisplayName("Should return 204 and delete kitchen type")
        void shouldReturn204AndDeleteKitchenType() throws Exception {
            // Given - First create a kitchen type
            KitchenTypeRequest createRequest = createKitchenTypeRequest("FRENCH");

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

            // Verify deletion
            assertThat(kitchenTypeRepository.findById(UUID.fromString(kitchenTypeId))).isEmpty();
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
