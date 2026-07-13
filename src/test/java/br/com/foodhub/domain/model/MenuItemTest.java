package br.com.foodhub.domain.model;

import br.com.foodhub.domain.exception.InvalidMenuItemPriceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MenuItem Domain Tests")
class MenuItemTest {

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
    }

    @Nested
    @DisplayName("Create MenuItem")
    class CreateMenuItemTests {

        @Test
        @DisplayName("Should create item correctly")
        void shouldCreateItemCorrectly() {
            // Given
            String name = "Pizza";
            String description = "Delicious pizza";
            BigDecimal price = new BigDecimal("25.00");
            boolean availableOnlyInRestaurant = false;
            String imagePath = "/images/pizza.jpg";

            // When
            MenuItem menuItem = MenuItem.create(
                    name,
                    description,
                    price,
                    availableOnlyInRestaurant,
                    imagePath,
                    restaurant
            );

            // Then
            assertThat(menuItem).isNotNull();
            assertThat(menuItem.getName()).isEqualTo(name);
            assertThat(menuItem.getDescription()).isEqualTo(description);
            assertThat(menuItem.getPrice()).isEqualTo(price);
            assertThat(menuItem.isAvailableOnlyInRestaurant()).isEqualTo(availableOnlyInRestaurant);
            assertThat(menuItem.getImagePath()).isEqualTo(imagePath);
            assertThat(menuItem.getRestaurant()).isEqualTo(restaurant);
        }

        @Test
        @DisplayName("Should throw InvalidMenuItemPriceException when price is zero")
        void shouldThrowInvalidMenuItemPriceExceptionWhenPriceIsZero() {
            // Given
            String name = "Pizza";
            String description = "Delicious pizza";
            BigDecimal price = BigDecimal.ZERO;
            boolean availableOnlyInRestaurant = false;
            String imagePath = "/images/pizza.jpg";

            // When & Then
            assertThatThrownBy(() -> MenuItem.create(
                    name,
                    description,
                    price,
                    availableOnlyInRestaurant,
                    imagePath,
                    restaurant
            ))
                    .isInstanceOf(InvalidMenuItemPriceException.class)
                    .hasMessageContaining("Invalid menu item price");
        }

        @Test
        @DisplayName("Should throw InvalidMenuItemPriceException when price is negative")
        void shouldThrowInvalidMenuItemPriceExceptionWhenPriceIsNegative() {
            // Given
            String name = "Pizza";
            String description = "Delicious pizza";
            BigDecimal price = new BigDecimal("-10.00");
            boolean availableOnlyInRestaurant = false;
            String imagePath = "/images/pizza.jpg";

            // When & Then
            assertThatThrownBy(() -> MenuItem.create(
                    name,
                    description,
                    price,
                    availableOnlyInRestaurant,
                    imagePath,
                    restaurant
            ))
                    .isInstanceOf(InvalidMenuItemPriceException.class)
                    .hasMessageContaining("Invalid menu item price");
        }

        @Test
        @DisplayName("Should throw InvalidMenuItemPriceException when price is null")
        void shouldThrowInvalidMenuItemPriceExceptionWhenPriceIsNull() {
            // Given
            String name = "Pizza";
            String description = "Delicious pizza";
            BigDecimal price = null;
            boolean availableOnlyInRestaurant = false;
            String imagePath = "/images/pizza.jpg";

            // When & Then
            assertThatThrownBy(() -> MenuItem.create(
                    name,
                    description,
                    price,
                    availableOnlyInRestaurant,
                    imagePath,
                    restaurant
            ))
                    .isInstanceOf(InvalidMenuItemPriceException.class)
                    .hasMessageContaining("Invalid menu item price");
        }
    }

    @Nested
    @DisplayName("Update MenuItem")
    class UpdateMenuItemTests {

        @Test
        @DisplayName("Should update item correctly")
        void shouldUpdateItemCorrectly() {
            // Given
            MenuItem menuItem = MenuItem.create(
                    "Old Name",
                    "Old Description",
                    new BigDecimal("20.00"),
                    false,
                    "/images/old.jpg",
                    restaurant
            );

            String newName = "New Name";
            String newDescription = "New Description";
            BigDecimal newPrice = new BigDecimal("30.00");
            boolean newAvailableOnlyInRestaurant = true;
            String newImagePath = "/images/new.jpg";

            // When
            menuItem.update(
                    newName,
                    newDescription,
                    newPrice,
                    newAvailableOnlyInRestaurant,
                    newImagePath,
                    restaurant
            );

            // Then
            assertThat(menuItem.getName()).isEqualTo(newName);
            assertThat(menuItem.getDescription()).isEqualTo(newDescription);
            assertThat(menuItem.getPrice()).isEqualTo(newPrice);
            assertThat(menuItem.isAvailableOnlyInRestaurant()).isEqualTo(newAvailableOnlyInRestaurant);
            assertThat(menuItem.getImagePath()).isEqualTo(newImagePath);
            assertThat(menuItem.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should throw InvalidMenuItemPriceException when updating with zero price")
        void shouldThrowInvalidMenuItemPriceExceptionWhenUpdatingWithZeroPrice() {
            // Given
            MenuItem menuItem = MenuItem.create(
                    "Pizza",
                    "Delicious pizza",
                    new BigDecimal("25.00"),
                    false,
                    "/images/pizza.jpg",
                    restaurant
            );

            // When & Then
            assertThatThrownBy(() -> menuItem.update(
                    "New Name",
                    "New Description",
                    BigDecimal.ZERO,
                    false,
                    "/images/new.jpg",
                    restaurant
            ))
                    .isInstanceOf(InvalidMenuItemPriceException.class)
                    .hasMessageContaining("Invalid menu item price");
        }

        @Test
        @DisplayName("Should throw InvalidMenuItemPriceException when updating with negative price")
        void shouldThrowInvalidMenuItemPriceExceptionWhenUpdatingWithNegativePrice() {
            // Given
            MenuItem menuItem = MenuItem.create(
                    "Pizza",
                    "Delicious pizza",
                    new BigDecimal("25.00"),
                    false,
                    "/images/pizza.jpg",
                    restaurant
            );

            // When & Then
            assertThatThrownBy(() -> menuItem.update(
                    "New Name",
                    "New Description",
                    new BigDecimal("-10.00"),
                    false,
                    "/images/new.jpg",
                    restaurant
            ))
                    .isInstanceOf(InvalidMenuItemPriceException.class)
                    .hasMessageContaining("Invalid menu item price");
        }

        @Test
        @DisplayName("Should throw InvalidMenuItemPriceException when updating with null price")
        void shouldThrowInvalidMenuItemPriceExceptionWhenUpdatingWithNullPrice() {
            // Given
            MenuItem menuItem = MenuItem.create(
                    "Pizza",
                    "Delicious pizza",
                    new BigDecimal("25.00"),
                    false,
                    "/images/pizza.jpg",
                    restaurant
            );

            // When & Then
            assertThatThrownBy(() -> menuItem.update(
                    "New Name",
                    "New Description",
                    null,
                    false,
                    "/images/new.jpg",
                    restaurant
            ))
                    .isInstanceOf(InvalidMenuItemPriceException.class)
                    .hasMessageContaining("Invalid menu item price");
        }
    }
}
