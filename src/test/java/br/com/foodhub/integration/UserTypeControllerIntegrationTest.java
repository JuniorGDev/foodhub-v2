package br.com.foodhub.integration;

import br.com.foodhub.infrastructure.repository.UserTypeRepository;
import br.com.foodhub.presentation.request.UserTypeRequest;
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
@DisplayName("User Type Controller Integration Tests")
class UserTypeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserTypeRepository userTypeRepository;

    private UUID adminUserTypeId;
    private UUID ownerUserTypeId;
    private UUID customerUserTypeId;

    @BeforeEach
    void setUp() {
        adminUserTypeId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        ownerUserTypeId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        customerUserTypeId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    }

    private UserTypeRequest createUserTypeRequest(String name) {
        return new UserTypeRequest(name);
    }

    @Nested
    @DisplayName("GET /api/v1/user-types")
    class FindAllUserTypesTests {

        @Test
        @DisplayName("Should return 200 with list of user types")
        void shouldReturn200WithListOfUserTypes() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/user-types"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/user-types/{id}")
    class FindUserTypeByIdTests {

        @Test
        @DisplayName("Should return 200 when user type exists")
        void shouldReturn200WhenUserTypeExists() throws Exception {
            // Given
            UUID userTypeId = adminUserTypeId;

            // When & Then
            mockMvc.perform(get("/api/v1/user-types/{id}", userTypeId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userTypeId.toString()))
                    .andExpect(jsonPath("$.name").exists());
        }

        @Test
        @DisplayName("Should return 404 when user type not found")
        void shouldReturn404WhenUserTypeNotFound() throws Exception {
            // Given
            UUID nonExistentUserTypeId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(get("/api/v1/user-types/{id}", nonExistentUserTypeId))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when UUID is invalid")
        void shouldReturn400WhenUUIDIsInvalid() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/user-types/{id}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/user-types")
    class CreateUserTypeTests {

        @Test
        @DisplayName("Should return 201 and persist user type")
        void shouldReturn201AndPersistUserType() throws Exception {
            // Given
            UserTypeRequest request = createUserTypeRequest("MANAGER");

            // When & Then
            String response = mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(request.name()))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Verify persistence
            String userTypeId = objectMapper.readTree(response).get("id").asText();
            assertThat(userTypeRepository.findById(UUID.fromString(userTypeId))).isPresent();
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UserTypeRequest request = createUserTypeRequest("");

            // When & Then
            mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void shouldReturn400WhenNameIsTooShort() throws Exception {
            // Given
            UserTypeRequest request = createUserTypeRequest("ab");

            // When & Then
            mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when name already exists")
        void shouldReturn409WhenNameAlreadyExists() throws Exception {
            // Given
            UserTypeRequest request = createUserTypeRequest("ADMIN");

            // When & Then
            mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/user-types/{id}")
    class UpdateUserTypeTests {

        @Test
        @DisplayName("Should return 200 and update user type")
        void shouldReturn200AndUpdateUserType() throws Exception {
            // Given
            UUID userTypeId = customerUserTypeId;
            UserTypeRequest request = createUserTypeRequest("VIP");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", userTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(request.name()));

            // Verify persistence
            assertThat(userTypeRepository.findById(userTypeId)).isPresent();
            assertThat(userTypeRepository.findById(userTypeId).get().getName()).isEqualTo(request.name());
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UUID userTypeId = customerUserTypeId;
            UserTypeRequest request = createUserTypeRequest("");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", userTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void shouldReturn400WhenNameIsTooShort() throws Exception {
            // Given
            UUID userTypeId = customerUserTypeId;
            UserTypeRequest request = createUserTypeRequest("ab");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", userTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when user type not found")
        void shouldReturn404WhenUserTypeNotFound() throws Exception {
            // Given
            UUID nonExistentUserTypeId = UUID.randomUUID();
            UserTypeRequest request = createUserTypeRequest("MANAGER");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", nonExistentUserTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when name already exists")
        void shouldReturn409WhenNameAlreadyExists() throws Exception {
            // Given
            UUID userTypeId = customerUserTypeId;
            UserTypeRequest request = createUserTypeRequest("ADMIN");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", userTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/user-types/{id}")
    class DeleteUserTypeTests {

        @Test
        @DisplayName("Should return 204 and delete user type")
        void shouldReturn204AndDeleteUserType() throws Exception {
            // Given - First create a user type
            UserTypeRequest createRequest = createUserTypeRequest("TEMP");

            String response = mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String userTypeId = objectMapper.readTree(response).get("id").asText();

            // When & Then
            mockMvc.perform(delete("/api/v1/user-types/{id}", userTypeId))
                    .andExpect(status().isNoContent());

            // Verify deletion
            assertThat(userTypeRepository.findById(UUID.fromString(userTypeId))).isEmpty();
        }

        @Test
        @DisplayName("Should return 404 when user type not found")
        void shouldReturn404WhenUserTypeNotFound() throws Exception {
            // Given
            UUID nonExistentUserTypeId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/api/v1/user-types/{id}", nonExistentUserTypeId))
                    .andExpect(status().isNotFound());
        }
    }
}
