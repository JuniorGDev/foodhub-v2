package br.com.foodhub.application.service;

import br.com.foodhub.application.dto.menuItem.MenuItemDTO;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.model.MenuItem;
import br.com.foodhub.domain.model.Restaurant;
import br.com.foodhub.infrastructure.repository.MenuItemRepository;
import br.com.foodhub.infrastructure.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MenuItemService {

    private final MenuItemRepository repository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.repository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<MenuItem> findAll() {
        return repository.findAll();
    }

    public MenuItem findById(UUID id) {
        return findMenuItem(id);
    }

    @Transactional
    public MenuItem save(MenuItemDTO menuItemDTO) {
        var restaurant = findRestaurant(menuItemDTO.restaurantId());
        var menuItem = MenuItem.create(
                menuItemDTO.name(),
                menuItemDTO.description(),
                menuItemDTO.price(),
                menuItemDTO.availableOnlyInRestaurant(),
                menuItemDTO.imagePath(),
                restaurant
        );
        return repository.save(menuItem);
    }

    @Transactional
    public MenuItem update(UUID id, MenuItemDTO menuItemDTO) {
        var menuItem = findMenuItem(id);
        var restaurant = findRestaurant(menuItemDTO.restaurantId());
        menuItem.update(
                menuItemDTO.name(),
                menuItemDTO.description(),
                menuItemDTO.price(),
                menuItemDTO.availableOnlyInRestaurant(),
                menuItemDTO.imagePath(),
                restaurant
        );
        return repository.save(menuItem);
    }

    @Transactional
    public void delete(UUID id) {
        var menuItem = findMenuItem(id);
        repository.delete(menuItem);
    }

    private MenuItem findMenuItem(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item", id));
    }

    private Restaurant findRestaurant(UUID id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", id));
    }
}
