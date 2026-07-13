package br.com.foodhub.application.service;

import br.com.foodhub.application.dto.user.CreateUserDTO;
import br.com.foodhub.application.dto.user.UpdateUserDTO;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.exception.UserAlreadyExistsException;
import br.com.foodhub.domain.model.User;
import br.com.foodhub.domain.model.UserType;
import br.com.foodhub.infrastructure.repository.UserRepository;
import br.com.foodhub.infrastructure.repository.UserTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final UserTypeRepository userTypeRepository;

    public UserService(
            UserRepository userRepository,
            UserTypeRepository userTypeRepository
    ) {
        this.repository = userRepository;
        this.userTypeRepository = userTypeRepository;
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(UUID id) {
        return findUser(id);
    }

    public User save(CreateUserDTO userDTO) {
        var userType = findUserType(userDTO.userTypeId());
        validateUniqueEmail(userDTO.email(), null);
        var user = User.create(
                userDTO.name(),
                userDTO.email(),
                userDTO.password(),
                userDTO.address(),
                userType
        );
        return repository.save(user);
    }

    public User update(UUID id, UpdateUserDTO userDTO) {
        var userType = findUserType(userDTO.userTypeId());
        validateUniqueEmail(userDTO.email(), id);
        var user = findUser(id);
        user.update(userDTO.name(), userDTO.email(), userDTO.address(), userType);
        return repository.save(user);
    }

    public void delete(UUID id) {
        var user = findUser(id);
        repository.delete(user);
    }

    private User findUser(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private UserType findUserType(UUID id) {
        return userTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User type", id));
    }

    private void validateUniqueEmail(String email, UUID id) {
        boolean exists = id == null
                ? repository.existsByEmail(email)
                : repository.existsByEmailAndIdNot(email, id);

        if (exists) {
            throw new UserAlreadyExistsException(email);
        }
    }
}
