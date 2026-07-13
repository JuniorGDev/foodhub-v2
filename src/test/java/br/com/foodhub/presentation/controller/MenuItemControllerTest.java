package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.dto.menuItem.MenuItemDTO;
import br.com.foodhub.application.mapper.MenuItemMapper;
import br.com.foodhub.application.service.MenuItemService;
import br.com.foodhub.domain.exception.InvalidMenuItemPriceException;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.model.MenuItem;
import br.com.foodhub.domain.model.Restaurant;
import br.com.foodhub.presentation.request.MenuItemRequest;
import br.com.foodhub.presentation.response.MenuItemResponse;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuItemController.class)
@DisplayName("MenuItemController Tests")
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuItemService menuItemService;

    @MockBean
    private MenuItemMapper menuItemMapper;

    private UUID menuItemId;
    private UUID restaurantId;
    private MenuItem menuItem;
    private MenuItemResponse menuItemResponse;
    private MenuItemRequest menuItemRequest;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        menuItemId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        
        restaurant = new Restaurant();
        menuItem = MenuItem.create(
                "Pizza",
                "Delicious pizza",
                new BigDecimal("25.00"),
                false,
                "/images/pizza.jpg",
                restaurant
        );
        
        menuItemResponse = new MenuItemResponse(
                menuItemId,
                "Pizza",
                "Delicious pizza",
                new BigDecimal("25.00"),
                false,
                "/images/pizza.jpg"
        );
        
        menuItemRequest = new MenuItemRequest(
                "Pizza",
                "Delicious pizza",
                new BigDecimal("25.00"),
                false,
                "/images/pizza.jpg",
                restaurantId
        );
    }

    @Nested
    @DisplayName("GET /api/v1/menu-items")
    class FindAllTests {

        @Test
        @DisplayName("Should return all menu items with status 200")
        void shouldReturnAllMenuItemsWithStatus200() throws Exception {
            // Given
            when(menuItemService.findAll()).thenReturn(List.of(menuItem));
            when(menuItemMapper.toResponseList(any())).thenReturn(List.of(menuItemResponse));

            // When & Then
            mockMvc.perform(get("/api/v1/menu-items"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value(menuItemId.toString()))
                    .andExpect(jsonPath("$[0].name").value(menuItem.getName()));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/menu-items/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Should return menu item by ID with status 200")
        void shouldReturnMenuItemByIdWithStatus200() throws Exception {
            // Given
            when(menuItemService.findById(menuItemId)).thenReturn(menuItem);
            when(menuItemMapper.toResponse(menuItem)).thenReturn(menuItemResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/menu-items/{id}", menuItemId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(menuItemId.toString()))
                    .andExpect(jsonPath("$.name").value(menuItem.getName()));
        }

        @Test
        @DisplayName("Should return 404 when menu item not found")
        void shouldReturn404WhenMenuItemNotFound() throws Exception {
            // Given
            when(menuItemService.findById(menuItemId)).thenThrow(new ResourceNotFoundException("Menu item", menuItemId));

            // When & Then
            mockMvc.perform(get("/api/v1/menu-items/{id}", menuItemId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/menu-items")
    class SaveTests {

        @Test
        @DisplayName("Should create menu item with status 201")
        void shouldCreateMenuItemWithStatus201() throws Exception {
            // Given
            MenuItemDTO menuItemDTO = new MenuItemDTO(
                    menuItemRequest.name(),
                    menuItemRequest.description(),
                    menuItemRequest.price(),
                    menuItemRequest.availableOnlyInRestaurant(),
                    menuItemRequest.imagePath(),
                    menuItemRequest.restaurantId()
            );
            
            when(menuItemMapper.toDTO(any(MenuItemRequest.class))).thenReturn(menuItemDTO);
            when(menuItemService.save(any(MenuItemDTO.class))).thenReturn(menuItem);
            when(menuItemMapper.toResponse(menuItem)).thenReturn(menuItemResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(menuItemRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(menuItemId.toString()))
                    .andExpect(jsonPath("$.name").value(menuItem.getName()));
        }

        @Test
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            MenuItemRequest invalidRequest = new MenuItemRequest("", "", null, false, "", null);

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when restaurant not found")
        void shouldReturn404WhenRestaurantNotFound() throws Exception {
            // Given
            MenuItemDTO menuItemDTO = new MenuItemDTO(
                    menuItemRequest.name(),
                    menuItemRequest.description(),
                    menuItemRequest.price(),
                    menuItemRequest.availableOnlyInRestaurant(),
                    menuItemRequest.imagePath(),
                    menuItemRequest.restaurantId()
            );
            
            when(menuItemMapper.toDTO(any(MenuItemRequest.class))).thenReturn(menuItemDTO);
            when(menuItemService.save(any(MenuItemDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Restaurant", restaurantId));

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(menuItemRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 422 when price is invalid")
        void shouldReturn422WhenPriceIsInvalid() throws Exception {
            // Given
            MenuItemDTO menuItemDTO = new MenuItemDTO(
                    menuItemRequest.name(),
                    menuItemRequest.description(),
                    menuItemRequest.price(),
                    menuItemRequest.availableOnlyInRestaurant(),
                    menuItemRequest.imagePath(),
                    menuItemRequest.restaurantId()
            );
            
            when(menuItemMapper.toDTO(any(MenuItemRequest.class))).thenReturn(menuItemDTO);
            when(menuItemService.save(any(MenuItemDTO.class)))
                    .thenThrow(new InvalidMenuItemPriceException());

            // When & Then
            mockMvc.perform(post("/api/v1/menu-items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(menuItemRequest)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/menu-items/{id}")
    class UpdateTests {

        @Test
        @DisplayName("Should update menu item with status 200")
        void shouldUpdateMenuItemWithStatus200() throws Exception {
            // Given
            MenuItemDTO menuItemDTO = new MenuItemDTO(
                    menuItemRequest.name(),
                    menuItemRequest.description(),
                    menuItemRequest.price(),
                    menuItemRequest.availableOnlyInRestaurant(),
                    menuItemRequest.imagePath(),
                    menuItemRequest.restaurantId()
            );
            
            when(menuItemMapper.toDTO(any(MenuItemRequest.class))).thenReturn(menuItemDTO);
            when(menuItemService.update(eq(menuItemId), any(MenuItemDTO.class))).thenReturn(menuItem);
            when(menuItemMapper.toResponse(menuItem)).thenReturn(menuItemResponse);

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(menuItemRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(menuItemId.toString()))
                    .andExpect(jsonPath("$.name").value(menuItem.getName()));
        }

        @Test
        @DisplayName("Should return 400 when request is invalid")
        void shouldReturn400WhenRequestIsInvalid() throws Exception {
            // Given
            MenuItemRequest invalidRequest = new MenuItemRequest("", "", null, false, "", null);

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 when menu item not found")
        void shouldReturn404WhenMenuItemNotFound() throws Exception {
            // Given
            MenuItemDTO menuItemDTO = new MenuItemDTO(
                    menuItemRequest.name(),
                    menuItemRequest.description(),
                    menuItemRequest.price(),
                    menuItemRequest.availableOnlyInRestaurant(),
                    menuItemRequest.imagePath(),
                    menuItemRequest.restaurantId()
            );
            
            when(menuItemMapper.toDTO(any(MenuItemRequest.class))).thenReturn(menuItemDTO);
            when(menuItemService.update(eq(menuItemId), any(MenuItemDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Menu item", menuItemId));

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(menuItemRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 422 when price is invalid")
        void shouldReturn422WhenPriceIsInvalid() throws Exception {
            // Given
            MenuItemDTO menuItemDTO = new MenuItemDTO(
                    menuItemRequest.name(),
                    menuItemRequest.description(),
                    menuItemRequest.price(),
                    menuItemRequest.availableOnlyInRestaurant(),
                    menuItemRequest.imagePath(),
                    menuItemRequest.restaurantId()
            );
            
            when(menuItemMapper.toDTO(any(MenuItemRequest.class))).thenReturn(menuItemDTO);
            when(menuItemService.update(eq(menuItemId), any(MenuItemDTO.class)))
                    .thenThrow(new InvalidMenuItemPriceException());

            // When & Then
            mockMvc.perform(put("/api/v1/menu-items/{id}", menuItemId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(menuItemRequest)))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/menu-items/{id}")
    class DeleteTests {

        @Test
        @DisplayName("Should delete menu item with status 204")
        void shouldDeleteMenuItemWithStatus204() throws Exception {
            // Given
            when(menuItemService.findById(menuItemId)).thenReturn(menuItem);
            org.mockito.Mockito.doNothing().when(menuItemService).delete(menuItemId);

            // When & Then
            mockMvc.perform(delete("/api/v1/menu-items/{id}", menuItemId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when menu item not found")
        void shouldReturn404WhenMenuItemNotFound() throws Exception {

            // Given
            doThrow(new ResourceNotFoundException("Menu item", menuItemId))
                    .when(menuItemService)
                    .delete(menuItemId);

            // When & Then
            mockMvc.perform(delete("/api/v1/menu-items/{id}", menuItemId))
                    .andExpect(status().isNotFound());
        }
    }
}
