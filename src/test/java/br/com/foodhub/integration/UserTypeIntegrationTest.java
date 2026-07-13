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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("UserType Integration Tests")
class UserTypeIntegrationTest {

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

    @Nested
    @DisplayName("POST /api/v1/user-types")
    class CreateUserTypeTests {

        @Test
        @DisplayName("Should create user type with status 201")
        void shouldCreateUserTypeWithStatus201() throws Exception {
            // Given
            UserTypeRequest request = new UserTypeRequest("MANAGER");

            // When & Then
            mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("MANAGER"));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UserTypeRequest request = new UserTypeRequest("");

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
            UserTypeRequest request = new UserTypeRequest("ab");

            // When & Then
            mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when user type already exists")
        void shouldReturn409WhenUserTypeAlreadyExists() throws Exception {
            // Given - First create a new user type with a unique name
            String uniqueName = "TEMP_" + System.currentTimeMillis();
            UserTypeRequest createRequest = new UserTypeRequest(uniqueName);
            mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)));

            UserTypeRequest request = new UserTypeRequest(uniqueName);

            // When & Then
            mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/user-types")
    class FindAllUserTypesTests {

        @Test
        @DisplayName("Should return all user types with status 200")
        void shouldReturnAllUserTypesWithStatus200() throws Exception {
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
        @DisplayName("Should return user type by ID with status 200")
        void shouldReturnUserTypeByIdWithStatus200() throws Exception {
            // Given
            // When & Then
            mockMvc.perform(get("/api/v1/user-types/{id}", adminUserTypeId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(adminUserTypeId.toString()))
                    .andExpect(jsonPath("$.name").value("ADMIN"));
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
    }

    @Nested
    @DisplayName("PUT /api/v1/user-types/{id}")
    class UpdateUserTypeTests {

        @Test
        @DisplayName("Should update user type with status 200")
        void shouldUpdateUserTypeWithStatus200() throws Exception {
            // Given - First create a new user type
            UserTypeRequest createRequest = new UserTypeRequest("TEMP");
            String response = mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String userTypeId = objectMapper.readTree(response).get("id").asText();

            UserTypeRequest updateRequest = new UserTypeRequest("UPDATED");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", userTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("UPDATED"));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UserTypeRequest request = new UserTypeRequest("");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", adminUserTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void shouldReturn400WhenNameIsTooShort() throws Exception {
            // Given
            UserTypeRequest request = new UserTypeRequest("ab");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", adminUserTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when user type not found")
        void shouldReturn404WhenUserTypeNotFound() throws Exception {
            // Given
            UUID nonExistentUserTypeId = UUID.randomUUID();
            UserTypeRequest request = new UserTypeRequest("UPDATED");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", nonExistentUserTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when user type already exists")
        void shouldReturn409WhenUserTypeAlreadyExists() throws Exception {
            // Given
            UserTypeRequest request = new UserTypeRequest("OWNER");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", adminUserTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/user-types/{id}")
    class DeleteUserTypeTests {

        @Test
        @DisplayName("Should delete user type with status 204")
        void shouldDeleteUserTypeWithStatus204() throws Exception {
            // Given - First create a new user type
            UserTypeRequest createRequest = new UserTypeRequest("TEMP");
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
