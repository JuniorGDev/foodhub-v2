package br.com.foodhub.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tb_user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 255)
    private String address;

    @ManyToOne
    @JoinColumn(name = "user_type_id", nullable = false)
    private UserType userType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public User(
            String name,
            String email,
            String password,
            String address,
            UserType userType
    ) {
        this.name = name;
        this.email = normalizeEmail(email);
        this.password = password;
        this.address = address;
        this.userType = userType;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void update(
            String name,
            String email,
            String address,
            UserType userType
    ) {
        this.name = name;
        this.email = normalizeEmail(email);
        this.address = address;
        this.userType = userType;

        this.updatedAt = LocalDateTime.now();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
