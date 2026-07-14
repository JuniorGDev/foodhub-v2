package br.com.foodhub.application.service;

import br.com.foodhub.domain.exception.ResourceInUseException;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.exception.UserTypeAlreadyExistsException;
import br.com.foodhub.domain.model.UserType;
import br.com.foodhub.infrastructure.repository.UserTypeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserTypeService {

    private final UserTypeRepository repository;

    public UserTypeService(UserTypeRepository userTypeRepository) {
        this.repository = userTypeRepository;
    }

    public List<UserType> findAll() {
        return repository.findAll();
    }

    public UserType findById(UUID id) {
        return findUserType(id);
    }

    public UserType save(String name) {
        var userType = UserType.create(name);
        validateUniqueName(userType.getName(), null);
        return repository.save(userType);
    }

    public UserType update(UUID id, String name) {
        var userType = findUserType(id);
        validateUniqueName(name, id);
        userType.update(name);
        return repository.save(userType);
    }

    public void delete(UUID id) {
        UserType userType = findUserType(id);
        try {
            repository.delete(userType);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceInUseException("User type");
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

    private UserType findUserType(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User type", id));
    }
}
