package br.com.foodhub.integration;

import br.com.foodhub.infrastructure.repository.UserRepository;
import br.com.foodhub.infrastructure.repository.UserTypeRepository;
import br.com.foodhub.presentation.request.UserRequest;
import br.com.foodhub.presentation.request.UserUpdateRequest;
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
@DisplayName("User Integration Tests")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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
    @DisplayName("POST /api/v1/users")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user with status 201")
        void shouldCreateUserWithStatus201() throws Exception {
            // Given
            UserRequest request = new UserRequest(
                    "New User",
                    "newuser@example.com",
                    "password123",
                    "123 Main St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(request.name()))
                    .andExpect(jsonPath("$.email").value(request.email().toLowerCase()))
                    .andExpect(jsonPath("$.address").value(request.address()));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UserRequest request = new UserRequest(
                    "",
                    "newuser@example.com",
                    "password123",
                    "123 Main St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void shouldReturn400WhenNameIsTooShort() throws Exception {
            // Given
            UserRequest request = new UserRequest(
                    "ab",
                    "newuser@example.com",
                    "password123",
                    "123 Main St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400WhenEmailIsInvalid() throws Exception {
            // Given
            UserRequest request = new UserRequest(
                    "New User",
                    "invalid-email",
                    "password123",
                    "123 Main St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when password is blank")
        void shouldReturn400WhenPasswordIsBlank() throws Exception {
            // Given
            UserRequest request = new UserRequest(
                    "New User",
                    "newuser@example.com",
                    "",
                    "123 Main St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when password is too short")
        void shouldReturn400WhenPasswordIsTooShort() throws Exception {
            // Given
            UserRequest request = new UserRequest(
                    "New User",
                    "newuser@example.com",
                    "1234567",
                    "123 Main St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when address is blank")
        void shouldReturn400WhenAddressIsBlank() throws Exception {
            // Given
            UserRequest request = new UserRequest(
                    "New User",
                    "newuser@example.com",
                    "password123",
                    "",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when userTypeId is null")
        void shouldReturn400WhenUserTypeIdIsNull() throws Exception {
            // Given
            UserRequest request = new UserRequest(
                    "New User",
                    "newuser@example.com",
                    "password123",
                    "123 Main St",
                    null
            );

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when email already exists")
        void shouldReturn409WhenEmailAlreadyExists() throws Exception {
            // Given
            UserRequest request = new UserRequest(
                    "New User",
                    "customer@foodhub.com",
                    "password123",
                    "123 Main St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should return 404 when user type not found")
        void shouldReturn404WhenUserTypeNotFound() throws Exception {
            // Given
            UUID nonExistentUserTypeId = UUID.randomUUID();
            UserRequest request = new UserRequest(
                    "New User",
                    "newuser@example.com",
                    "password123",
                    "123 Main St",
                    nonExistentUserTypeId
            );

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/users")
    class FindAllUsersTests {

        @Test
        @DisplayName("Should return all users with status 200")
        void shouldReturnAllUsersWithStatus200() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/users/{id}")
    class FindUserByIdTests {

        @Test
        @DisplayName("Should return user by ID with status 200")
        void shouldReturnUserByIdWithStatus200() throws Exception {
            // Given
            UUID customerId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

            // When & Then
            mockMvc.perform(get("/api/v1/users/{id}", customerId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(customerId.toString()))
                    .andExpect(jsonPath("$.name").exists())
                    .andExpect(jsonPath("$.email").exists());
        }

        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            UUID nonExistentUserId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(get("/api/v1/users/{id}", nonExistentUserId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/users/{id}")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user with status 200")
        void shouldUpdateUserWithStatus200() throws Exception {
            // Given
            UUID customerId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
            UserUpdateRequest request = new UserUpdateRequest(
                    "Updated Name",
                    "updated@example.com",
                    "456 Updated St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", customerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value(request.name()))
                    .andExpect(jsonPath("$.email").value(request.email().toLowerCase()))
                    .andExpect(jsonPath("$.address").value(request.address()));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // Given
            UUID customerId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
            UserUpdateRequest request = new UserUpdateRequest(
                    "",
                    "updated@example.com",
                    "456 Updated St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", customerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400WhenEmailIsInvalid() throws Exception {
            // Given
            UUID customerId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
            UserUpdateRequest request = new UserUpdateRequest(
                    "Updated Name",
                    "invalid-email",
                    "456 Updated St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", customerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when address is blank")
        void shouldReturn400WhenAddressIsBlank() throws Exception {
            // Given
            UUID customerId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
            UserUpdateRequest request = new UserUpdateRequest(
                    "Updated Name",
                    "updated@example.com",
                    "",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", customerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when userTypeId is null")
        void shouldReturn400WhenUserTypeIdIsNull() throws Exception {
            // Given
            UUID customerId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
            UserUpdateRequest request = new UserUpdateRequest(
                    "Updated Name",
                    "updated@example.com",
                    "456 Updated St",
                    null
            );

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", customerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            UUID nonExistentUserId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest(
                    "Updated Name",
                    "updated@example.com",
                    "456 Updated St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", nonExistentUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when email already exists")
        void shouldReturn409WhenEmailAlreadyExists() throws Exception {
            // Given
            UUID customerId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
            UserUpdateRequest request = new UserUpdateRequest(
                    "Updated Name",
                    "admin@foodhub.com",
                    "456 Updated St",
                    customerUserTypeId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", customerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should return 404 when user type not found")
        void shouldReturn404WhenUserTypeNotFound() throws Exception {
            // Given
            UUID customerId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
            UUID nonExistentUserTypeId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest(
                    "Updated Name",
                    "updated@example.com",
                    "456 Updated St",
                    nonExistentUserTypeId
            );

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", customerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/users/{id}")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user with status 204")
        void shouldDeleteUserWithStatus204() throws Exception {
            // Given - First create a user
            UserRequest createRequest = new UserRequest(
                    "To Delete",
                    "todelete@example.com",
                    "password123",
                    "123 Delete St",
                    customerUserTypeId
            );

            String response = mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String userId = objectMapper.readTree(response).get("id").asText();

            // When & Then
            mockMvc.perform(delete("/api/v1/users/{id}", userId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            UUID nonExistentUserId = UUID.randomUUID();

            // When & Then
            mockMvc.perform(delete("/api/v1/users/{id}", nonExistentUserId))
                    .andExpect(status().isNotFound());
        }
    }
}
