package br.com.foodhub.domain.model;

import br.com.foodhub.domain.exception.InvalidMenuItemPriceException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "menu_item")
public class MenuItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 250)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean availableOnlyInRestaurant;

    @Column(nullable = false)
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public MenuItem(
            String name,
            String description,
            BigDecimal price,
            boolean availableOnlyInRestaurant,
            String imagePath,
            Restaurant restaurant
    ) {
        update(name, description, price, availableOnlyInRestaurant, imagePath, restaurant);
        this.createdAt = LocalDateTime.now();
    }

    public void update(
            String name,
            String description,
            BigDecimal price,
            boolean availableOnlyInRestaurant,
            String imagePath,
            Restaurant restaurant
    ) {
        validatePrice(price);
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableOnlyInRestaurant = availableOnlyInRestaurant;
        this.imagePath = imagePath;
        this.restaurant = restaurant;
        this.updatedAt = LocalDateTime.now();
    }

    public static MenuItem create(
            String name,
            String description,
            BigDecimal price,
            boolean availableOnlyInRestaurant,
            String imagePath,
            Restaurant restaurant
    ) {
        return new MenuItem(name, description, price, availableOnlyInRestaurant, imagePath, restaurant);
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMenuItemPriceException();
        }
    }
}
