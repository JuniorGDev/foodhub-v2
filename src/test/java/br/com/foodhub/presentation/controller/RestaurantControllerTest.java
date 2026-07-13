package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.dto.restaurant.RestaurantDTO;
import br.com.foodhub.application.mapper.MenuItemMapper;
import br.com.foodhub.application.mapper.RestaurantMapper;
import br.com.foodhub.application.service.RestaurantService;
import br.com.foodhub.domain.exception.InvalidRestaurantOwnerException;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.model.KitchenType;
import br.com.foodhub.domain.model.Restaurant;
import br.com.foodhub.domain.model.User;
import br.com.foodhub.domain.model.UserType;
import br.com.foodhub.presentation.request.RestaurantRequest;
import br.com.foodhub.presentation.response.RestaurantResponse;
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

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
@DisplayName("RestaurantController Tests")
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private RestaurantMapper restaurantMapper;

    @MockBean
    private MenuItemMapper menuItemMapper;

    private UUID restaurantId;
    private UUID kitchenTypeId;
    private UUID ownerId;
    private Restaurant restaurant;
    private RestaurantResponse restaurantResponse;
    private RestaurantRequest restaurantRequest;
    private KitchenType kitchenType;
    private User owner;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        kitchenTypeId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        
        kitchenType = new KitchenType("Italian");
        UserType ownerType = new UserType("OWNER");
        owner = User.create("John Owner", "owner@example.com", "password123", "123 Owner St", ownerType);
        
        restaurant = Restaurant.create(
                "Restaurant Name",
                kitchenType,
                "123 Restaurant St",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                owner
        );
        
        restaurantResponse = new RestaurantResponse(
                restaurantId,
                "Restaurant Name",
                "123 Restaurant St",
                "italian",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                "John Owner",
                List.of()
        );
        
        restaurantRequest = new RestaurantRequest(
                "Restaurant Name",
                kitchenTypeId,
                "123 Restaurant St",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                ownerId
        );
    }

    @Nested
    @DisplayName("GET /api/v1/restaurants")
    class FindAllTests {

        @Test
        @DisplayName("Should return all restaurants with status 200")
        void shouldReturnAllRestaurantsWithStatus200() throws Exception {
            // Given
            when(restaurantService.findAll()).thenReturn(List.of(restaurant));
            when(restaurantMapper.toResponseList(any())).thenReturn(List.of(restaurantResponse));

            // When & Then
            mockMvc.perform(get("/api/v1/restaurants"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(restaurantId.toString()))
                    .andExpect(jsonPath("$[0].name").value(restaurant.getName()));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/restaurants/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Should return restaurant by ID with status 200")
        void shouldReturnRestaurantByIdWithStatus200() throws Exception {
            // Given
            when(restaurantService.findById(restaurantId)).thenReturn(restaurant);
            when(restaurantMapper.toResponse(restaurant)).thenReturn(restaurantResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/restaurants/{id}", restaurantId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                    .andExpect(jsonPath("$.name").value(restaurant.getName()));
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            // Given
            when(restaurantService.findById(restaurantId)).thenThrow(new ResourceNotFoundException("Restaurant", restaurantId));

            // When & Then
            mockMvc.perform(get("/api/v1/restaurants/{id}", restaurantId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/restaurants")
    class SaveTests {

        @Test
        @DisplayName("Should create restaurant with status 201")
        void shouldCreateRestaurantWithStatus201() throws Exception {
            // Given
            RestaurantDTO restaurantDTO = new RestaurantDTO(
                    restaurantRequest.name(),
                    restaurantRequest.address(),
                    restaurantRequest.kitchenTypeId(),
                    restaurantRequest.openingTime(),
                    restaurantRequest.closingTime(),
                    restaurantRequest.ownerId()
            );
            
            when(restaurantMapper.toDTO(any(RestaurantRequest.class))).thenReturn(restaurantDTO);
            when(restaurantService.save(any(RestaurantDTO.class))).thenReturn(restaurant);
            when(restaurantMapper.toResponse(restaurant)).thenReturn(restaurantResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(restaurantRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                    .andExpect(jsonPath("$.name").value(restaurant.getName()));
        }

        @Test
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            RestaurantRequest invalidRequest = new RestaurantRequest("", null, "", null, null, null);

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when kitchen type or owner not found")
        void shouldReturn404WhenKitchenTypeOrOwnerNotFound() throws Exception {
            // Given
            RestaurantDTO restaurantDTO = new RestaurantDTO(
                    restaurantRequest.name(),
                    restaurantRequest.address(),
                    restaurantRequest.kitchenTypeId(),
                    restaurantRequest.openingTime(),
                    restaurantRequest.closingTime(),
                    restaurantRequest.ownerId()
            );
            
            when(restaurantMapper.toDTO(any(RestaurantRequest.class))).thenReturn(restaurantDTO);
            when(restaurantService.save(any(RestaurantDTO.class)))
                    .thenThrow(new ResourceNotFoundException("KitchenType", kitchenTypeId));

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(restaurantRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 422 when user is not OWNER")
        void shouldReturn422WhenUserIsNotOwner() throws Exception {
            // Given
            RestaurantDTO restaurantDTO = new RestaurantDTO(
                    restaurantRequest.name(),
                    restaurantRequest.address(),
                    restaurantRequest.kitchenTypeId(),
                    restaurantRequest.openingTime(),
                    restaurantRequest.closingTime(),
                    restaurantRequest.ownerId()
            );
            
            when(restaurantMapper.toDTO(any(RestaurantRequest.class))).thenReturn(restaurantDTO);
            when(restaurantService.save(any(RestaurantDTO.class)))
                    .thenThrow(new InvalidRestaurantOwnerException());

            // When & Then
            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(restaurantRequest)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/restaurants/{id}")
    class UpdateTests {

        @Test
        @DisplayName("Should update restaurant with status 200")
        void shouldUpdateRestaurantWithStatus200() throws Exception {
            // Given
            RestaurantDTO restaurantDTO = new RestaurantDTO(
                    restaurantRequest.name(),
                    restaurantRequest.address(),
                    restaurantRequest.kitchenTypeId(),
                    restaurantRequest.openingTime(),
                    restaurantRequest.closingTime(),
                    restaurantRequest.ownerId()
            );
            
            when(restaurantMapper.toDTO(any(RestaurantRequest.class))).thenReturn(restaurantDTO);
            when(restaurantService.update(eq(restaurantId), any(RestaurantDTO.class))).thenReturn(restaurant);
            when(restaurantMapper.toResponse(restaurant)).thenReturn(restaurantResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(restaurantRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(restaurantId.toString()))
                    .andExpect(jsonPath("$.name").value(restaurant.getName()));
        }

        @Test
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            RestaurantRequest invalidRequest = new RestaurantRequest("", null, "", null, null, null);

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            // Given
            RestaurantDTO restaurantDTO = new RestaurantDTO(
                    restaurantRequest.name(),
                    restaurantRequest.address(),
                    restaurantRequest.kitchenTypeId(),
                    restaurantRequest.openingTime(),
                    restaurantRequest.closingTime(),
                    restaurantRequest.ownerId()
            );
            
            when(restaurantMapper.toDTO(any(RestaurantRequest.class))).thenReturn(restaurantDTO);
            when(restaurantService.update(eq(restaurantId), any(RestaurantDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Restaurant", restaurantId));

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(restaurantRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 422 when user is not OWNER")
        void shouldReturn422WhenUserIsNotOwner() throws Exception {
            // Given
            RestaurantDTO restaurantDTO = new RestaurantDTO(
                    restaurantRequest.name(),
                    restaurantRequest.address(),
                    restaurantRequest.kitchenTypeId(),
                    restaurantRequest.openingTime(),
                    restaurantRequest.closingTime(),
                    restaurantRequest.ownerId()
            );
            
            when(restaurantMapper.toDTO(any(RestaurantRequest.class))).thenReturn(restaurantDTO);
            when(restaurantService.update(eq(restaurantId), any(RestaurantDTO.class)))
                    .thenThrow(new InvalidRestaurantOwnerException());

            // When & Then
            mockMvc.perform(put("/api/v1/restaurants/{id}", restaurantId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(restaurantRequest)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/restaurants/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Should delete restaurant with status 204")
        void shouldDeleteRestaurantWithStatus204() throws Exception {
            // Given
            when(restaurantService.findById(restaurantId)).thenReturn(restaurant);
            org.mockito.Mockito.doNothing().when(restaurantService).delete(restaurantId);

            // When & Then
            mockMvc.perform(delete("/api/v1/restaurants/{id}", restaurantId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            // Given
            doThrow(new ResourceNotFoundException("Restaurant", restaurantId))
                    .when(restaurantService)
                    .delete(restaurantId);

            // When & Then
            mockMvc.perform(delete("/api/v1/restaurants/{id}", restaurantId))
                    .andExpect(status().isNotFound());
        }
    }
}
