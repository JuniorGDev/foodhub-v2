package br.com.foodhub.domain.model;

import br.com.foodhub.domain.exception.InvalidBusinessHoursException;
import br.com.foodhub.domain.exception.InvalidRestaurantOwnerException;
import br.com.foodhub.shared.constants.ValidationConstants;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "restaurant")
public class Restaurant implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = ValidationConstants.NAME_MAX_LENGTH)
    private String name;

    @ManyToOne
    @JoinColumn(name = "kitchen_type_id", nullable = false)
    private KitchenType kitchenType;

    @Column(nullable = false, length = ValidationConstants.ADDRESS_MAX_LENGTH)
    private String address;

    @Column(nullable = false)
    private LocalTime openingTime;

    @Column(nullable = false)
    private LocalTime closingTime;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
    private Set<MenuItem> menuItems = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    public Restaurant(
            String name,
            KitchenType kitchenType,
            String address,
            LocalTime openingTime,
            LocalTime closingTime,
            User owner
    ){
        update(name, kitchenType, address, openingTime, closingTime, owner);
        this.createdAt = LocalDateTime.now();
    }

    public void update(
            String name,
            KitchenType kitchenType,
            String address,
            LocalTime openingTime,
            LocalTime closingTime,
            User owner
    ) {
        validateOwner(owner);
        validateBusinessHours(openingTime, closingTime);
        this.name = name;
        this.kitchenType = kitchenType;
        this.address = address;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.owner = owner;

        this.updatedAt = LocalDateTime.now();
    }

    public static Restaurant create(
            String name,
            KitchenType kitchenType,
            String address,
            LocalTime openingTime,
            LocalTime closingTime,
            User owner
    ) {
        return new Restaurant(name, kitchenType, address, openingTime, closingTime, owner);
    }

    private void validateOwner(User owner) {
        if (!owner.isOwner()) {
            throw new InvalidRestaurantOwnerException();
        }
    }

    private void validateBusinessHours(
            LocalTime opening,
            LocalTime closing
    ) {
        if (!opening.isBefore(closing)) {
            throw new InvalidBusinessHoursException();
        }
    }
}
