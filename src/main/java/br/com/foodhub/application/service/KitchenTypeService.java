package br.com.foodhub.application.service;

import br.com.foodhub.domain.exception.ResourceInUseException;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.exception.UserTypeAlreadyExistsException;
import br.com.foodhub.domain.model.KitchenType;
import br.com.foodhub.infrastructure.repository.KitchenTypeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KitchenTypeService {

    private final KitchenTypeRepository repository;

    public KitchenTypeService(KitchenTypeRepository kitchenTypeRepository) {
        this.repository = kitchenTypeRepository;
    }

    public List<KitchenType> findAll() {
        return repository.findAll();
    }

    public KitchenType findById(UUID id) {
        return findKitchenType(id);
    }

    public KitchenType save(String name) {
        var kitchenType = KitchenType.create(name);
        validateUniqueName(kitchenType.getName(), null);
        return repository.save(kitchenType);
    }

    public KitchenType update(UUID id, String name) {
        var kitchenType = findKitchenType(id);
        validateUniqueName(name, id);
        kitchenType.update(name);
        return repository.save(kitchenType);
    }

    public void delete(UUID id) {
        var kitchenType = findKitchenType(id);
        try {
            repository.delete(kitchenType);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceInUseException("Kitchen type");
        }
    }

    private void validateUniqueName(String name, UUID id) {
        boolean exists = id == null
                ? repository.existsByName(name)
                : repository.existsByNameAndIdNot(name, id);

        if (exists) {
            throw new UserTypeAlreadyExistsException(name);
        }
    }

    private KitchenType findKitchenType(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kitchen type", id));
    }
}
