package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.mapper.KitchenTypeMapper;
import br.com.foodhub.application.service.KitchenTypeService;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.exception.UserTypeAlreadyExistsException;
import br.com.foodhub.domain.model.KitchenType;
import br.com.foodhub.presentation.request.KitchenTypeRequest;
import br.com.foodhub.presentation.response.KitchenTypeResponse;
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

@WebMvcTest(KitchenTypeController.class)
@DisplayName("KitchenTypeController Tests")
class KitchenTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KitchenTypeService kitchenTypeService;

    @MockBean
    private KitchenTypeMapper kitchenTypeMapper;

    private UUID kitchenTypeId;
    private KitchenType kitchenType;
    private KitchenTypeResponse kitchenTypeResponse;
    private KitchenTypeRequest kitchenTypeRequest;

    @BeforeEach
    void setUp() {
        kitchenTypeId = UUID.randomUUID();
        kitchenType = new KitchenType("Italian");
        kitchenTypeResponse = new KitchenTypeResponse(kitchenTypeId, "italian");
        kitchenTypeRequest = new KitchenTypeRequest("Italian");
    }

    @Nested
    @DisplayName("GET /api/v1/kitchen-types")
    class FindAllTests {

        @Test
        @DisplayName("Should return all kitchen types with status 200")
        void shouldReturnAllKitchenTypesWithStatus200() throws Exception {
            // Given
            when(kitchenTypeService.findAll()).thenReturn(List.of(kitchenType));
            when(kitchenTypeMapper.toResponseList(any())).thenReturn(List.of(kitchenTypeResponse));

            // When & Then
            mockMvc.perform(get("/api/v1/kitchen-types"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(kitchenTypeId.toString()))
                    .andExpect(jsonPath("$[0].name").value(kitchenType.getName()));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/kitchen-types/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Should return kitchen type by ID with status 200")
        void shouldReturnKitchenTypeByIdWithStatus200() throws Exception {
            // Given
            when(kitchenTypeService.findById(kitchenTypeId)).thenReturn(kitchenType);
            when(kitchenTypeMapper.toResponse(kitchenType)).thenReturn(kitchenTypeResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/kitchen-types/{id}", kitchenTypeId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(kitchenTypeId.toString()))
                    .andExpect(jsonPath("$.name").value(kitchenType.getName()));
        }

        @Test
        @DisplayName("Should return 404 when kitchen type not found")
        void shouldReturn404WhenKitchenTypeNotFound() throws Exception {
            // Given
            when(kitchenTypeService.findById(kitchenTypeId)).thenThrow(new ResourceNotFoundException("Kitchen type", kitchenTypeId));

            // When & Then
            mockMvc.perform(get("/api/v1/kitchen-types/{id}", kitchenTypeId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/kitchen-types")
    class SaveTests {

        @Test
        @DisplayName("Should create kitchen type with status 201")
        void shouldCreateKitchenTypeWithStatus201() throws Exception {
            // Given
            when(kitchenTypeService.save(any(String.class))).thenReturn(kitchenType);
            when(kitchenTypeMapper.toResponse(kitchenType)).thenReturn(kitchenTypeResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(kitchenTypeRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(kitchenTypeId.toString()))
                    .andExpect(jsonPath("$.name").value(kitchenType.getName()));
        }

        @Test
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            KitchenTypeRequest invalidRequest = new KitchenTypeRequest("");

            // When & Then
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 409 when kitchen type already exists")
        void shouldReturn409WhenKitchenTypeAlreadyExists() throws Exception {
            // Given
            when(kitchenTypeService.save(any(String.class))).thenThrow(new UserTypeAlreadyExistsException("Italian"));

            // When & Then
            mockMvc.perform(post("/api/v1/kitchen-types")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(kitchenTypeRequest)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/kitchen-types/{id}")
    class UpdateTests {

        @Test
        @DisplayName("Should update kitchen type with status 200")
        void shouldUpdateKitchenTypeWithStatus200() throws Exception {
            // Given
            when(kitchenTypeService.update(eq(kitchenTypeId), any(String.class))).thenReturn(kitchenType);
            when(kitchenTypeMapper.toResponse(kitchenType)).thenReturn(kitchenTypeResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", kitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(kitchenTypeRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(kitchenTypeId.toString()))
                    .andExpect(jsonPath("$.name").value(kitchenType.getName()));
        }

        @Test
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            KitchenTypeRequest invalidRequest = new KitchenTypeRequest("");

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", kitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when kitchen type not found")
        void shouldReturn404WhenKitchenTypeNotFound() throws Exception {
            // Given
            when(kitchenTypeService.update(eq(kitchenTypeId), any(String.class)))
                    .thenThrow(new ResourceNotFoundException("Kitchen type", kitchenTypeId));

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", kitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(kitchenTypeRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when kitchen type already exists")
        void shouldReturn409WhenKitchenTypeAlreadyExists() throws Exception {
            // Given
            when(kitchenTypeService.update(eq(kitchenTypeId), any(String.class)))
                    .thenThrow(new UserTypeAlreadyExistsException("Mexican"));

            // When & Then
            mockMvc.perform(put("/api/v1/kitchen-types/{id}", kitchenTypeId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(kitchenTypeRequest)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/kitchen-types/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Should delete kitchen type with status 204")
        void shouldDeleteKitchenTypeWithStatus204() throws Exception {
            // Given
            when(kitchenTypeService.findById(kitchenTypeId)).thenReturn(kitchenType);
            org.mockito.Mockito.doNothing().when(kitchenTypeService).delete(kitchenTypeId);

            // When & Then
            mockMvc.perform(delete("/api/v1/kitchen-types/{id}", kitchenTypeId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when kitchen type not found")
        void shouldReturn404WhenKitchenTypeNotFound() throws Exception {

            // Given
            doThrow(new ResourceNotFoundException("Kitchen type", kitchenTypeId))
                    .when(kitchenTypeService)
                    .delete(kitchenTypeId);

            // When & Then
            mockMvc.perform(delete("/api/v1/kitchen-types/{id}", kitchenTypeId))
                    .andExpect(status().isNotFound());
        }
    }
}
