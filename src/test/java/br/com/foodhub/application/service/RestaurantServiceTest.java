package br.com.foodhub.application.service;

import br.com.foodhub.application.dto.restaurant.RestaurantDTO;
import br.com.foodhub.domain.exception.InvalidRestaurantOwnerException;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.model.KitchenType;
import br.com.foodhub.domain.model.Restaurant;
import br.com.foodhub.domain.model.User;
import br.com.foodhub.domain.model.UserType;
import br.com.foodhub.infrastructure.repository.KitchenTypeRepository;
import br.com.foodhub.infrastructure.repository.RestaurantRepository;
import br.com.foodhub.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestaurantService Tests")
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private KitchenTypeRepository kitchenTypeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private UUID restaurantId;
    private UUID kitchenTypeId;
    private UUID ownerId;
    private Restaurant restaurant;
    private KitchenType kitchenType;
    private User owner;
    private RestaurantDTO restaurantDTO;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        kitchenTypeId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        
        kitchenType = createKitchenType();
        owner = createOwner();
        restaurant = createRestaurant(kitchenType, owner);
        restaurantDTO = createRestaurantDTO(kitchenTypeId, ownerId);
    }

    @Nested
    @DisplayName("Create Restaurant")
    class CreateRestaurantTests {

        @Test
        @DisplayName("Should create restaurant successfully")
        void shouldCreateRestaurantSuccessfully() {
            // Given
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.of(kitchenType));
            when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
            when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

            // When
            Restaurant result = restaurantService.save(restaurantDTO);

            // Then
            assertThat(result).isNotNull();
            
            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(userRepository).findById(ownerId);
            verify(restaurantRepository).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw InvalidRestaurantOwnerException when user is not OWNER")
        void shouldThrowInvalidRestaurantOwnerExceptionWhenUserIsNotOwner() {
            // Given
            User regularUser = createRegularUser();
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.of(kitchenType));
            when(userRepository.findById(ownerId)).thenReturn(Optional.of(regularUser));

            // When & Then
            assertThatThrownBy(() -> restaurantService.save(restaurantDTO))
                    .isInstanceOf(InvalidRestaurantOwnerException.class)
                    .hasMessageContaining("Only OWNER users can own a restaurant.");

            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(userRepository).findById(ownerId);
            verify(restaurantRepository, never()).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when KitchenType not found")
        void shouldThrowResourceNotFoundExceptionWhenKitchenTypeNotFound() {
            // Given
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> restaurantService.save(restaurantDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("KitchenType not found with id: " + kitchenTypeId);

            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(userRepository, never()).findById(any(UUID.class));
            verify(restaurantRepository, never()).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when User not found")
        void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
            // Given
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.of(kitchenType));
            when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> restaurantService.save(restaurantDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + ownerId);

            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(userRepository).findById(ownerId);
            verify(restaurantRepository, never()).save(any(Restaurant.class));
        }
    }

    @Nested
    @DisplayName("Find Restaurant By ID")
    class FindRestaurantByIdTests {

        @Test
        @DisplayName("Should find restaurant by ID")
        void shouldFindRestaurantById() {
            // Given
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

            // When
            Restaurant result = restaurantService.findById(restaurantId);

            // Then
            assertThat(result).isNotNull();
            
            verify(restaurantRepository).findById(restaurantId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when restaurant not found")
        void shouldThrowResourceNotFoundExceptionWhenRestaurantNotFound() {
            // Given
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> restaurantService.findById(restaurantId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Restaurant not found with id: " + restaurantId);

            verify(restaurantRepository).findById(restaurantId);
        }
    }

    @Nested
    @DisplayName("Update Restaurant")
    class UpdateRestaurantTests {

        @Test
        @DisplayName("Should update restaurant successfully")
        void shouldUpdateRestaurantSuccessfully() {
            // Given
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.of(kitchenType));
            when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
            when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

            // When
            Restaurant result = restaurantService.update(restaurantId, restaurantDTO);

            // Then
            assertThat(result).isNotNull();
            
            verify(restaurantRepository).findById(restaurantId);
            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(userRepository).findById(ownerId);
            verify(restaurantRepository).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw InvalidRestaurantOwnerException when user is not OWNER during update")
        void shouldThrowInvalidRestaurantOwnerExceptionWhenUserIsNotOwnerDuringUpdate() {
            // Given
            User regularUser = createRegularUser();
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.of(kitchenType));
            when(userRepository.findById(ownerId)).thenReturn(Optional.of(regularUser));

            // When & Then
            assertThatThrownBy(() -> restaurantService.update(restaurantId, restaurantDTO))
                    .isInstanceOf(InvalidRestaurantOwnerException.class)
                    .hasMessageContaining("Only OWNER users can own a restaurant.");

            verify(restaurantRepository).findById(restaurantId);
            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(userRepository).findById(ownerId);
            verify(restaurantRepository, never()).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when KitchenType not found during update")
        void shouldThrowResourceNotFoundExceptionWhenKitchenTypeNotFoundDuringUpdate() {
            // Given
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> restaurantService.update(restaurantId, restaurantDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("KitchenType not found with id: " + kitchenTypeId);

            verify(restaurantRepository).findById(restaurantId);
            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(userRepository, never()).findById(any(UUID.class));
            verify(restaurantRepository, never()).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when User not found during update")
        void shouldThrowResourceNotFoundExceptionWhenUserNotFoundDuringUpdate() {
            // Given
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.of(kitchenType));
            when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> restaurantService.update(restaurantId, restaurantDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + ownerId);

            verify(restaurantRepository).findById(restaurantId);
            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(userRepository).findById(ownerId);
            verify(restaurantRepository, never()).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when restaurant not found during update")
        void shouldThrowResourceNotFoundExceptionWhenRestaurantNotFoundDuringUpdate() {
            // Given
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> restaurantService.update(restaurantId, restaurantDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Restaurant not found with id: " + restaurantId);

            verify(restaurantRepository).findById(restaurantId);
            verify(kitchenTypeRepository, never()).findById(any(UUID.class));
            verify(userRepository, never()).findById(any(UUID.class));
            verify(restaurantRepository, never()).save(any(Restaurant.class));
        }
    }

    @Nested
    @DisplayName("Delete Restaurant")
    class DeleteRestaurantTests {

        @Test
        @DisplayName("Should delete restaurant successfully")
        void shouldDeleteRestaurantSuccessfully() {
            // Given
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
            doNothing().when(restaurantRepository).delete(restaurant);

            // When
            restaurantService.delete(restaurantId);

            // Then
            verify(restaurantRepository).findById(restaurantId);
            verify(restaurantRepository).delete(restaurant);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when restaurant not found during delete")
        void shouldThrowResourceNotFoundExceptionWhenRestaurantNotFoundDuringDelete() {
            // Given
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> restaurantService.delete(restaurantId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Restaurant not found with id: " + restaurantId);

            verify(restaurantRepository).findById(restaurantId);
            verify(restaurantRepository, never()).delete(any(Restaurant.class));
        }
    }

    // Private helper methods for creating test objects

    private KitchenType createKitchenType() {
        return new KitchenType("Italian");
    }

    private UserType createOwnerUserType() {
        return new UserType("OWNER");
    }

    private UserType createRegularUserType() {
        return new UserType("USER");
    }

    private User createOwner() {
        return User.create(
                "John Owner",
                "owner@example.com",
                "password123",
                "123 Owner St",
                createOwnerUserType()
        );
    }

    private User createRegularUser() {
        return User.create(
                "John User",
                "user@example.com",
                "password123",
                "123 User St",
                createRegularUserType()
        );
    }

    private Restaurant createRestaurant(KitchenType kitchenType, User owner) {
        return Restaurant.create(
                "Restaurant Name",
                kitchenType,
                "123 Restaurant St",
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                owner
        );
    }

    private RestaurantDTO createRestaurantDTO(UUID kitchenTypeId, UUID ownerId) {
        return new RestaurantDTO(
                "Restaurant Name",
                "123 Restaurant St",
                kitchenTypeId,
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                ownerId
        );
    }
}
