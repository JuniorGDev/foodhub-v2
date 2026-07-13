package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.mapper.UserTypeMapper;
import br.com.foodhub.application.service.UserTypeService;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.exception.UserTypeAlreadyExistsException;
import br.com.foodhub.domain.model.UserType;
import br.com.foodhub.presentation.request.UserTypeRequest;
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

@WebMvcTest(UserTypeController.class)
@DisplayName("UserTypeController Tests")
class UserTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserTypeService userTypeService;

    @MockBean
    private UserTypeMapper userTypeMapper;

    private UUID userTypeId;
    private UserType userType;
    private UserTypeResponse userTypeResponse;
    private UserTypeRequest userTypeRequest;

    @BeforeEach
    void setUp() {
        userTypeId = UUID.randomUUID();
        userType = new UserType("OWNER");
        userTypeResponse = new UserTypeResponse(userTypeId, "OWNER");
        userTypeRequest = new UserTypeRequest("OWNER");
    }

    @Nested
    @DisplayName("GET /api/v1/user-types")
    class FindAllTests {

        @Test
        @DisplayName("Should return all user types with status 200")
        void shouldReturnAllUserTypesWithStatus200() throws Exception {
            // Given
            when(userTypeService.findAll()).thenReturn(List.of(userType));
            when(userTypeMapper.toResponseList(any())).thenReturn(List.of(userTypeResponse));

            // When & Then
            mockMvc.perform(get("/api/v1/user-types"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(userTypeId.toString()))
                    .andExpect(jsonPath("$[0].name").value(userType.getName()));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/user-types/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Should return user type by ID with status 200")
        void shouldReturnUserTypeByIdWithStatus200() throws Exception {
            // Given
            when(userTypeService.findById(userTypeId)).thenReturn(userType);
            when(userTypeMapper.toResponse(userType)).thenReturn(userTypeResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/user-types/{id}", userTypeId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userTypeId.toString()))
                    .andExpect(jsonPath("$.name").value(userType.getName()));
        }

        @Test
        @DisplayName("Should return 404 when user type not found")
        void shouldReturn404WhenUserTypeNotFound() throws Exception {
            // Given
            when(userTypeService.findById(userTypeId)).thenThrow(new ResourceNotFoundException("User type", userTypeId));

            // When & Then
            mockMvc.perform(get("/api/v1/user-types/{id}", userTypeId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/user-types")
    class SaveTests {

        @Test
        @DisplayName("Should create user type with status 201")
        void shouldCreateUserTypeWithStatus201() throws Exception {
            // Given
            when(userTypeService.save(any(String.class))).thenReturn(userType);
            when(userTypeMapper.toResponse(userType)).thenReturn(userTypeResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userTypeRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userTypeId.toString()))
                    .andExpect(jsonPath("$.name").value(userType.getName()));
        }

        @Test
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            UserTypeRequest invalidRequest = new UserTypeRequest("");

            // When & Then
            mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when user type already exists")
        void shouldReturn409WhenUserTypeAlreadyExists() throws Exception {
            // Given
            when(userTypeService.save(any(String.class))).thenThrow(new UserTypeAlreadyExistsException("OWNER"));

            // When & Then
            mockMvc.perform(post("/api/v1/user-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userTypeRequest)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/user-types/{id}")
    class UpdateTests {

        @Test
        @DisplayName("Should update user type with status 200")
        void shouldUpdateUserTypeWithStatus200() throws Exception {
            // Given
            when(userTypeService.update(eq(userTypeId), any(String.class))).thenReturn(userType);
            when(userTypeMapper.toResponse(userType)).thenReturn(userTypeResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", userTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userTypeRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userTypeId.toString()))
                    .andExpect(jsonPath("$.name").value(userType.getName()));
        }

        @Test
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            UserTypeRequest invalidRequest = new UserTypeRequest("");

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", userTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when user type not found")
        void shouldReturn404WhenUserTypeNotFound() throws Exception {
            // Given
            when(userTypeService.update(eq(userTypeId), any(String.class)))
                    .thenThrow(new ResourceNotFoundException("User type", userTypeId));

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", userTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userTypeRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when user type already exists")
        void shouldReturn409WhenUserTypeAlreadyExists() throws Exception {
            // Given
            when(userTypeService.update(eq(userTypeId), any(String.class)))
                    .thenThrow(new UserTypeAlreadyExistsException("ADMIN"));

            // When & Then
            mockMvc.perform(put("/api/v1/user-types/{id}", userTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userTypeRequest)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/user-types/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Should delete user type with status 204")
        void shouldDeleteUserTypeWithStatus204() throws Exception {
            // Given
            when(userTypeService.findById(userTypeId)).thenReturn(userType);
            org.mockito.Mockito.doNothing().when(userTypeService).delete(userTypeId);

            // When & Then
            mockMvc.perform(delete("/api/v1/user-types/{id}", userTypeId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when user type not found")
        void shouldReturn404WhenUserTypeNotFound() throws Exception {
            // Given
            doThrow(new ResourceNotFoundException("User type", userTypeId))
                    .when(userTypeService)
                    .delete(userTypeId);

            // When & Then
            mockMvc.perform(delete("/api/v1/user-types/{id}", userTypeId))
                    .andExpect(status().isNotFound());
        }
    }
}
