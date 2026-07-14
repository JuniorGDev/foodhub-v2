package br.com.foodhub.application.service;

import br.com.foodhub.application.dto.restaurant.RestaurantDTO;
import br.com.foodhub.domain.exception.ResourceInUseException;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.model.KitchenType;
import br.com.foodhub.domain.model.Restaurant;
import br.com.foodhub.domain.model.User;
import br.com.foodhub.infrastructure.repository.KitchenTypeRepository;
import br.com.foodhub.infrastructure.repository.RestaurantRepository;
import br.com.foodhub.infrastructure.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RestaurantService {

    private final RestaurantRepository repository;
    private final KitchenTypeRepository kitchenTypeRepository;
    private final UserRepository userRepository;

    public RestaurantService(
            RestaurantRepository restaurantRepository,
            KitchenTypeRepository kitchenTypeRepository,
            UserRepository userRepository
    ) {
        this.repository = restaurantRepository;
        this.kitchenTypeRepository = kitchenTypeRepository;
        this.userRepository = userRepository;
    }

    public List<Restaurant> findAll() {
        return repository.findAll();
    }

    public Restaurant findById(UUID id) {
        return findRestaurant(id);
    }

    @Transactional
    public Restaurant save(RestaurantDTO restaurantDTO) {
        var kitchenType = findKitchenType(restaurantDTO.kitchenTypeId());
        var owner = findUser(restaurantDTO.ownerId());
        var restaurant = Restaurant.create(
                restaurantDTO.name(),
                kitchenType,
                restaurantDTO.address(),
                restaurantDTO.openingTime(),
                restaurantDTO.closingTime(),
                owner
        );
        return repository.save(restaurant);
    }

    @Transactional
    public Restaurant update(UUID id, RestaurantDTO restaurantDTO) {
        var restaurant = findRestaurant(id);
        var kitchenType = findKitchenType(restaurantDTO.kitchenTypeId());
        var owner = findUser(restaurantDTO.ownerId());
        restaurant.update(
                restaurantDTO.name(),
                kitchenType,
                restaurantDTO.address(),
                restaurantDTO.openingTime(),
                restaurantDTO.closingTime(),
                owner
        );
        return repository.save(restaurant);
    }

    @Transactional
    public void delete(UUID id) {
        var restaurant = findRestaurant(id);
        try {
            repository.delete(restaurant);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceInUseException("Restaurant");
        }
    }

    private Restaurant findRestaurant(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));
    }

    private KitchenType findKitchenType(UUID id) {
        return kitchenTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("KitchenType", id));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
