package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.dto.user.CreateUserDTO;
import br.com.foodhub.application.dto.user.UpdateUserDTO;
import br.com.foodhub.application.mapper.UserMapper;
import br.com.foodhub.application.mapper.UserTypeMapper;
import br.com.foodhub.application.service.UserService;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.exception.UserAlreadyExistsException;
import br.com.foodhub.domain.model.User;
import br.com.foodhub.domain.model.UserType;
import br.com.foodhub.presentation.request.UserRequest;
import br.com.foodhub.presentation.request.UserUpdateRequest;
import br.com.foodhub.presentation.response.UserResponse;
import br.com.foodhub.presentation.response.UserTypeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserTypeMapper userTypeMapper;

    private UUID userId;
    private UUID userTypeId;
    private User user;
    private UserResponse userResponse;
    private UserType userType;
    private UserTypeResponse userTypeResponse;
    private UserRequest userRequest;
    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userTypeId = UUID.randomUUID();
        
        userType = new UserType("OWNER");
        userTypeResponse = new UserTypeResponse(userTypeId, "OWNER");
        
        user = createUser(userType);
        userResponse = createUserResponse(userId, userTypeResponse);
        
        userRequest = createUserRequest(userTypeId);
        userUpdateRequest = createUserUpdateRequest(userTypeId);
    }

    @Nested
    @DisplayName("GET /api/v1/users")
    class FindAllTests {

        @Test
        @DisplayName("Should return all users with status 200")
        void shouldReturnAllUsersWithStatus200() throws Exception {
            // Given
            when(userService.findAll()).thenReturn(List.of(user));
            when(userMapper.toResponseList(any())).thenReturn(List.of(userResponse));

            // When & Then
            mockMvc.perform(get("/api/v1/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(userId.toString()))
                    .andExpect(jsonPath("$[0].name").value(user.getName()))
                    .andExpect(jsonPath("$[0].email").value(user.getEmail()));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/users/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Should return user by ID with status 200")
        void shouldReturnUserByIdWithStatus200() throws Exception {
            // Given
            when(userService.findById(userId)).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/users/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userId.toString()))
                    .andExpect(jsonPath("$.name").value(user.getName()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()));
        }

        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            when(userService.findById(userId)).thenThrow(new ResourceNotFoundException("User", userId));

            // When & Then
            mockMvc.perform(get("/api/v1/users/{id}", userId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/users")
    class SaveTests {

        @Test
        @DisplayName("Should create user with status 201")
        void shouldCreateUserWithStatus201() throws Exception {
            // Given
            CreateUserDTO createUserDTO = new CreateUserDTO(
                    userRequest.name(),
                    userRequest.email(),
                    userRequest.password(),
                    userRequest.address(),
                    userRequest.userTypeId()
            );
            
            when(userMapper.toCreateDTO(any(UserRequest.class))).thenReturn(createUserDTO);
            when(userService.save(any(CreateUserDTO.class))).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userId.toString()))
                    .andExpect(jsonPath("$.name").value(user.getName()))
                    .andExpect(jsonPath("$.email").value(user.getEmail()));
        }

        @Test
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            UserRequest invalidRequest = new UserRequest("", "", "", "", null);

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when email already exists")
        void shouldReturn409WhenEmailAlreadyExists() throws Exception {
            // Given
            CreateUserDTO createUserDTO = new CreateUserDTO(
                    userRequest.name(),
                    userRequest.email(),
                    userRequest.password(),
                    userRequest.address(),
                    userRequest.userTypeId()
            );
            
            when(userMapper.toCreateDTO(any(UserRequest.class))).thenReturn(createUserDTO);
            when(userService.save(any(CreateUserDTO.class))).thenThrow(new UserAlreadyExistsException(userRequest.email()));

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRequest)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should return 404 when user type not found")
        void shouldReturn404WhenUserTypeNotFound() throws Exception {
            // Given
            CreateUserDTO createUserDTO = new CreateUserDTO(
                    userRequest.name(),
                    userRequest.email(),
                    userRequest.password(),
                    userRequest.address(),
                    userRequest.userTypeId()
            );
            
            when(userMapper.toCreateDTO(any(UserRequest.class))).thenReturn(createUserDTO);
            when(userService.save(any(CreateUserDTO.class))).thenThrow(new ResourceNotFoundException("User type", userTypeId));

            // When & Then
            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/users/{id}")
    class UpdateTests {

        @Test
        @DisplayName("Should update user with status 200")
        void shouldUpdateUserWithStatus200() throws Exception {
            // Given
            UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                    userUpdateRequest.name(),
                    userUpdateRequest.email(),
                    userUpdateRequest.address(),
                    userUpdateRequest.userTypeId()
            );
            
            when(userMapper.toUpdateDTO(any(UserUpdateRequest.class))).thenReturn(updateUserDTO);
            when(userService.update(eq(userId), any(UpdateUserDTO.class))).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userId.toString()))
                    .andExpect(jsonPath("$.name").value(user.getName()));
        }

        @Test
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            UserUpdateRequest invalidRequest = new UserUpdateRequest("", "", "", null);

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                    userUpdateRequest.name(),
                    userUpdateRequest.email(),
                    userUpdateRequest.address(),
                    userUpdateRequest.userTypeId()
            );
            
            when(userMapper.toUpdateDTO(any(UserUpdateRequest.class))).thenReturn(updateUserDTO);
            when(userService.update(eq(userId), any(UpdateUserDTO.class)))
                    .thenThrow(new ResourceNotFoundException("User", userId));

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdateRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when email already exists")
        void shouldReturn409WhenEmailAlreadyExists() throws Exception {
            // Given
            UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                    userUpdateRequest.name(),
                    userUpdateRequest.email(),
                    userUpdateRequest.address(),
                    userUpdateRequest.userTypeId()
            );
            
            when(userMapper.toUpdateDTO(any(UserUpdateRequest.class))).thenReturn(updateUserDTO);
            when(userService.update(eq(userId), any(UpdateUserDTO.class)))
                    .thenThrow(new UserAlreadyExistsException(userUpdateRequest.email()));

            // When & Then
            mockMvc.perform(put("/api/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdateRequest)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/users/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Should delete user with status 204")
        void shouldDeleteUserWithStatus204() throws Exception {
            // Given
            when(userService.findById(userId)).thenReturn(user);
            org.mockito.Mockito.doNothing().when(userService).delete(userId);

            // When & Then
            mockMvc.perform(delete("/api/v1/users/{id}", userId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when user not found")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            doThrow(new ResourceNotFoundException("User", userId))
                    .when(userService)
                    .delete(userId);

            // When & Then
            mockMvc.perform(delete("/api/v1/users/{id}", userId))
                    .andExpect(status().isNotFound());
        }
    }

    // Private helper methods for creating test objects

    private User createUser(UserType userType) {
        return User.create(
                "John Doe",
                "john.doe@example.com",
                "password123",
                "123 Main St",
                userType
        );
    }

    private UserResponse createUserResponse(UUID userId, UserTypeResponse userTypeResponse) {
        return new UserResponse(
                userId,
                "John Doe",
                "john.doe@example.com",
                "123 Main St",
                userTypeResponse
        );
    }

    private UserRequest createUserRequest(UUID userTypeId) {
        return new UserRequest(
                "John Doe",
                "john.doe@example.com",
                "password123",
                "123 Main St",
                userTypeId
        );
    }

    private UserUpdateRequest createUserUpdateRequest(UUID userTypeId) {
        return new UserUpdateRequest(
                "Jane Doe",
                "jane.doe@example.com",
                "456 Oak Ave",
                userTypeId
        );
    }
}
