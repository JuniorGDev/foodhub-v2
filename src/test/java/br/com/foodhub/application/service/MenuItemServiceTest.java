package br.com.foodhub.application.service;

import br.com.foodhub.application.dto.menuItem.MenuItemDTO;
import br.com.foodhub.domain.exception.InvalidMenuItemPriceException;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.model.MenuItem;
import br.com.foodhub.domain.model.Restaurant;
import br.com.foodhub.infrastructure.repository.MenuItemRepository;
import br.com.foodhub.infrastructure.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuItemService Tests")
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuItemService menuItemService;

    private UUID menuItemId;
    private UUID restaurantId;
    private MenuItem menuItem;
    private Restaurant restaurant;
    private MenuItemDTO menuItemDTO;

    @BeforeEach
    void setUp() {
        menuItemId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        
        restaurant = createRestaurant();
        menuItem = createMenuItem(restaurant);
        menuItemDTO = createMenuItemDTO(restaurantId);
    }

    @Nested
    @DisplayName("Create MenuItem")
    class CreateMenuItemTests {

        @Test
        @DisplayName("Should create menu item successfully")
        void shouldCreateMenuItemSuccessfully() {
            // Given
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
            when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

            // When
            MenuItem result = menuItemService.save(menuItemDTO);

            // Then
            assertThat(result).isNotNull();
            
            verify(restaurantRepository).findById(restaurantId);
            verify(menuItemRepository).save(any(MenuItem.class));
        }

        @Test
        @DisplayName("Should throw InvalidMenuItemPriceException when price is zero")
        void shouldThrowInvalidMenuItemPriceExceptionWhenPriceIsZero() {
            // Given
            MenuItemDTO invalidDTO = createMenuItemDTOWithPrice(restaurantId, BigDecimal.ZERO);
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

            // When & Then
            assertThatThrownBy(() -> menuItemService.save(invalidDTO))
                    .isInstanceOf(InvalidMenuItemPriceException.class)
                    .hasMessageContaining("Invalid menu item price");

            verify(restaurantRepository).findById(restaurantId);
            verify(menuItemRepository, never()).save(any(MenuItem.class));
        }

        @Test
        @DisplayName("Should throw InvalidMenuItemPriceException when price is negative")
        void shouldThrowInvalidMenuItemPriceExceptionWhenPriceIsNegative() {
            // Given
            MenuItemDTO invalidDTO = createMenuItemDTOWithPrice(restaurantId, new BigDecimal("-10.00"));
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

            // When & Then
            assertThatThrownBy(() -> menuItemService.save(invalidDTO))
                    .isInstanceOf(InvalidMenuItemPriceException.class)
                    .hasMessageContaining("Invalid menu item price");

            verify(restaurantRepository).findById(restaurantId);
            verify(menuItemRepository, never()).save(any(MenuItem.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when Restaurant not found")
        void shouldThrowResourceNotFoundExceptionWhenRestaurantNotFound() {
            // Given
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> menuItemService.save(menuItemDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Restaurant not found with id: " + restaurantId);

            verify(restaurantRepository).findById(restaurantId);
            verify(menuItemRepository, never()).save(any(MenuItem.class));
        }
    }

    @Nested
    @DisplayName("Find MenuItem By ID")
    class FindMenuItemByIdTests {

        @Test
        @DisplayName("Should find menu item by ID")
        void shouldFindMenuItemById() {
            // Given
            when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));

            // When
            MenuItem result = menuItemService.findById(menuItemId);

            // Then
            assertThat(result).isNotNull();
            
            verify(menuItemRepository).findById(menuItemId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when menu item not found")
        void shouldThrowResourceNotFoundExceptionWhenMenuItemNotFound() {
            // Given
            when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> menuItemService.findById(menuItemId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Menu item not found with id: " + menuItemId);

            verify(menuItemRepository).findById(menuItemId);
        }
    }

    @Nested
    @DisplayName("Update MenuItem")
    class UpdateMenuItemTests {

        @Test
        @DisplayName("Should update menu item successfully")
        void shouldUpdateMenuItemSuccessfully() {
            // Given
            when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
            when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

            // When
            MenuItem result = menuItemService.update(menuItemId, menuItemDTO);

            // Then
            assertThat(result).isNotNull();
            
            verify(menuItemRepository).findById(menuItemId);
            verify(restaurantRepository).findById(restaurantId);
            verify(menuItemRepository).save(any(MenuItem.class));
        }

        @Test
        @DisplayName("Should throw InvalidMenuItemPriceException when price is zero during update")
        void shouldThrowInvalidMenuItemPriceExceptionWhenPriceIsZeroDuringUpdate() {
            // Given
            MenuItemDTO invalidDTO = createMenuItemDTOWithPrice(restaurantId, BigDecimal.ZERO);
            when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

            // When & Then
            assertThatThrownBy(() -> menuItemService.update(menuItemId, invalidDTO))
                    .isInstanceOf(InvalidMenuItemPriceException.class)
                    .hasMessageContaining("Invalid menu item price");

            verify(menuItemRepository).findById(menuItemId);
            verify(restaurantRepository).findById(restaurantId);
            verify(menuItemRepository, never()).save(any(MenuItem.class));
        }

        @Test
        @DisplayName("Should throw InvalidMenuItemPriceException when price is negative during update")
        void shouldThrowInvalidMenuItemPriceExceptionWhenPriceIsNegativeDuringUpdate() {
            // Given
            MenuItemDTO invalidDTO = createMenuItemDTOWithPrice(restaurantId, new BigDecimal("-10.00"));
            when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

            // When & Then
            assertThatThrownBy(() -> menuItemService.update(menuItemId, invalidDTO))
                    .isInstanceOf(InvalidMenuItemPriceException.class)
                    .hasMessageContaining("Invalid menu item price");

            verify(menuItemRepository).findById(menuItemId);
            verify(restaurantRepository).findById(restaurantId);
            verify(menuItemRepository, never()).save(any(MenuItem.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when Restaurant not found during update")
        void shouldThrowResourceNotFoundExceptionWhenRestaurantNotFoundDuringUpdate() {
            // Given
            when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> menuItemService.update(menuItemId, menuItemDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Restaurant not found with id: " + restaurantId);

            verify(menuItemRepository).findById(menuItemId);
            verify(restaurantRepository).findById(restaurantId);
            verify(menuItemRepository, never()).save(any(MenuItem.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when menu item not found during update")
        void shouldThrowResourceNotFoundExceptionWhenMenuItemNotFoundDuringUpdate() {
            // Given
            when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> menuItemService.update(menuItemId, menuItemDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Menu item not found with id: " + menuItemId);

            verify(menuItemRepository).findById(menuItemId);
            verify(restaurantRepository, never()).findById(any(UUID.class));
            verify(menuItemRepository, never()).save(any(MenuItem.class));
        }
    }

    @Nested
    @DisplayName("Delete MenuItem")
    class DeleteMenuItemTests {

        @Test
        @DisplayName("Should delete menu item successfully")
        void shouldDeleteMenuItemSuccessfully() {
            // Given
            when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.of(menuItem));
            doNothing().when(menuItemRepository).delete(menuItem);

            // When
            menuItemService.delete(menuItemId);

            // Then
            verify(menuItemRepository).findById(menuItemId);
            verify(menuItemRepository).delete(menuItem);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when menu item not found during delete")
        void shouldThrowResourceNotFoundExceptionWhenMenuItemNotFoundDuringDelete() {
            // Given
            when(menuItemRepository.findById(menuItemId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> menuItemService.delete(menuItemId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Menu item not found with id: " + menuItemId);

            verify(menuItemRepository).findById(menuItemId);
            verify(menuItemRepository, never()).delete(any(MenuItem.class));
        }
    }

    // Private helper methods for creating test objects

    private Restaurant createRestaurant() {
        return new Restaurant();
    }

    private MenuItem createMenuItem(Restaurant restaurant) {
        return MenuItem.create(
                "Pizza",
                "Delicious pizza",
                new BigDecimal("25.00"),
                false,
                "/images/pizza.jpg",
                restaurant
        );
    }

    private MenuItemDTO createMenuItemDTO(UUID restaurantId) {
        return new MenuItemDTO(
                "Pizza",
                "Delicious pizza",
                new BigDecimal("25.00"),
                false,
                "/images/pizza.jpg",
                restaurantId
        );
    }

    private MenuItemDTO createMenuItemDTOWithPrice(UUID restaurantId, BigDecimal price) {
        return new MenuItemDTO(
                "Pizza",
                "Delicious pizza",
                price,
                false,
                "/images/pizza.jpg",
                restaurantId
        );
    }
}
