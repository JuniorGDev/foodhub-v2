package br.com.foodhub.domain.model;

import br.com.foodhub.domain.exception.InvalidBusinessHoursException;
import br.com.foodhub.domain.exception.InvalidRestaurantOwnerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Restaurant Domain Tests")
class RestaurantTest {

    private KitchenType kitchenType;
    private User owner;
    private User regularUser;

    @BeforeEach
    void setUp() {
        kitchenType = new KitchenType("Italian");
        owner = createOwnerUser();
        regularUser = createRegularUser();
    }

    @Nested
    @DisplayName("Create Restaurant")
    class CreateRestaurantTests {

        @Test
        @DisplayName("Should create restaurant when Owner is OWNER")
        void shouldCreateRestaurantWhenOwnerIsOwner() {
            // Given
            String name = "Restaurant Name";
            String address = "123 Restaurant St";
            LocalTime openingTime = LocalTime.of(10, 0);
            LocalTime closingTime = LocalTime.of(22, 0);

            // When
            Restaurant restaurant = Restaurant.create(
                    name,
                    kitchenType,
                    address,
                    openingTime,
                    closingTime,
                    owner
            );

            // Then
            assertThat(restaurant).isNotNull();
            assertThat(restaurant.getName()).isEqualTo(name);
            assertThat(restaurant.getKitchenType()).isEqualTo(kitchenType);
            assertThat(restaurant.getAddress()).isEqualTo(address);
            assertThat(restaurant.getOpeningTime()).isEqualTo(openingTime);
            assertThat(restaurant.getClosingTime()).isEqualTo(closingTime);
            assertThat(restaurant.getOwner()).isEqualTo(owner);
        }

        @Test
        @DisplayName("Should throw InvalidRestaurantOwnerException when user is not OWNER")
        void shouldThrowInvalidRestaurantOwnerExceptionWhenUserIsNotOwner() {
            // Given
            String name = "Restaurant Name";
            String address = "123 Restaurant St";
            LocalTime openingTime = LocalTime.of(10, 0);
            LocalTime closingTime = LocalTime.of(22, 0);

            // When & Then
            assertThatThrownBy(() -> Restaurant.create(
                    name,
                    kitchenType,
                    address,
                    openingTime,
                    closingTime,
                    regularUser
            ))
                    .isInstanceOf(InvalidRestaurantOwnerException.class)
                    .hasMessageContaining("Only OWNER users can own a restaurant.");
        }

        @Test
        @DisplayName("Should throw InvalidBusinessHoursException when opening time is after closing time")
        void shouldThrowInvalidBusinessHoursExceptionWhenOpeningTimeIsAfterClosingTime() {
            // Given
            String name = "Restaurant Name";
            String address = "123 Restaurant St";
            LocalTime openingTime = LocalTime.of(22, 0);
            LocalTime closingTime = LocalTime.of(10, 0);

            // When & Then
            assertThatThrownBy(() -> Restaurant.create(
                    name,
                    kitchenType,
                    address,
                    openingTime,
                    closingTime,
                    owner
            ))
                    .isInstanceOf(InvalidBusinessHoursException.class);
        }

        @Test
        @DisplayName("Should throw InvalidBusinessHoursException when opening time equals closing time")
        void shouldThrowInvalidBusinessHoursExceptionWhenOpeningTimeEqualsClosingTime() {
            // Given
            String name = "Restaurant Name";
            String address = "123 Restaurant St";
            LocalTime openingTime = LocalTime.of(12, 0);
            LocalTime closingTime = LocalTime.of(12, 0);

            // When & Then
            assertThatThrownBy(() -> Restaurant.create(
                    name,
                    kitchenType,
                    address,
                    openingTime,
                    closingTime,
                    owner
            ))
                    .isInstanceOf(InvalidBusinessHoursException.class);
        }
    }

    @Nested
    @DisplayName("Update Restaurant")
    class UpdateRestaurantTests {

        @Test
        @DisplayName("Should update restaurant correctly")
        void shouldUpdateRestaurantCorrectly() {
            // Given
            Restaurant restaurant = Restaurant.create(
                    "Old Name",
                    kitchenType,
                    "Old Address",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    owner
            );

            String newName = "New Name";
            String newAddress = "New Address";
            LocalTime newOpeningTime = LocalTime.of(11, 0);
            LocalTime newClosingTime = LocalTime.of(23, 0);

            // When
            restaurant.update(
                    newName,
                    kitchenType,
                    newAddress,
                    newOpeningTime,
                    newClosingTime,
                    owner
            );

            // Then
            assertThat(restaurant.getName()).isEqualTo(newName);
            assertThat(restaurant.getAddress()).isEqualTo(newAddress);
            assertThat(restaurant.getOpeningTime()).isEqualTo(newOpeningTime);
            assertThat(restaurant.getClosingTime()).isEqualTo(newClosingTime);
            assertThat(restaurant.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should throw InvalidRestaurantOwnerException when updating with non-owner user")
        void shouldThrowInvalidRestaurantOwnerExceptionWhenUpdatingWithNonOwnerUser() {
            // Given
            Restaurant restaurant = Restaurant.create(
                    "Restaurant Name",
                    kitchenType,
                    "123 Restaurant St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    owner
            );

            // When & Then
            assertThatThrownBy(() -> restaurant.update(
                    "New Name",
                    kitchenType,
                    "New Address",
                    LocalTime.of(11, 0),
                    LocalTime.of(23, 0),
                    regularUser
            ))
                    .isInstanceOf(InvalidRestaurantOwnerException.class)
                    .hasMessageContaining("Only OWNER users can own a restaurant.");
        }

        @Test
        @DisplayName("Should throw InvalidBusinessHoursException when updating with invalid hours")
        void shouldThrowInvalidBusinessHoursExceptionWhenUpdatingWithInvalidHours() {
            // Given
            Restaurant restaurant = Restaurant.create(
                    "Restaurant Name",
                    kitchenType,
                    "123 Restaurant St",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    owner
            );

            // When & Then
            assertThatThrownBy(() -> restaurant.update(
                    "New Name",
                    kitchenType,
                    "New Address",
                    LocalTime.of(22, 0),
                    LocalTime.of(10, 0),
                    owner
            ))
                    .isInstanceOf(InvalidBusinessHoursException.class);
        }
    }

    // Private helper methods for creating test objects

    private User createOwnerUser() {
        UserType ownerType = new UserType("OWNER");
        return User.create(
                "John Owner",
                "owner@example.com",
                "password123",
                "123 Owner St",
                ownerType
        );
    }

    private User createRegularUser() {
        UserType userType = new UserType("USER");
        return User.create(
                "John User",
                "user@example.com",
                "password123",
                "123 User St",
                userType
        );
    }
}
