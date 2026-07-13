package br.com.foodhub.infrastructure.repository;

import br.com.foodhub.domain.model.KitchenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KitchenTypeRepository extends JpaRepository<KitchenType, UUID> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, UUID id);
}
