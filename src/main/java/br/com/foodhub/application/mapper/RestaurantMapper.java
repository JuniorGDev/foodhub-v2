package br.com.foodhub.application.mapper;

import br.com.foodhub.application.dto.restaurant.RestaurantDTO;
import br.com.foodhub.domain.model.Restaurant;
import br.com.foodhub.presentation.request.RestaurantRequest;
import br.com.foodhub.presentation.response.RestaurantResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestaurantMapper {

    private final MenuItemMapper menuItemMapper;

    public RestaurantMapper(MenuItemMapper menuItemMapper) {
        this.menuItemMapper = menuItemMapper;
    }

    public RestaurantDTO toDTO(RestaurantRequest request) {
        return new RestaurantDTO(
                request.name(),
                request.address(),
                request.kitchenTypeId(),
                request.openingTime(),
                request.closingTime(),
                request.ownerId()
        );
    }

    public RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getKitchenType().getName(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                restaurant.getOwner().getName(),
                menuItemMapper.toResponseList(restaurant.getMenuItems().stream().toList())
        );
    }

    public List<RestaurantResponse> toResponseList(List<Restaurant> restaurants) {
        return restaurants.stream().map(this::toResponse).toList();
    }
}
