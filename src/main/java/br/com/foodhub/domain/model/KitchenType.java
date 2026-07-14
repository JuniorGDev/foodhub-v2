package br.com.foodhub.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "kitchen_type")
public class KitchenType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 150, unique = true)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public KitchenType(String name) {
        this.name = normalizedName(name);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    public void update(String name) {
        this.name = normalizedName(name);
        this.updatedAt = LocalDateTime.now();
    }

    private String normalizedName(String name){
        return name.trim().toUpperCase();
    };

    public static KitchenType create(String name) {
        return new KitchenType(name);
    }
}
