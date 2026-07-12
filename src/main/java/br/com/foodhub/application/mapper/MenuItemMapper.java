package br.com.foodhub.application.mapper;

import br.com.foodhub.application.dto.menuItem.MenuItemDTO;
import br.com.foodhub.domain.model.MenuItem;
import br.com.foodhub.presentation.request.MenuItemRequest;
import br.com.foodhub.presentation.response.MenuItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuItemMapper {
    public MenuItemDTO toDTO(MenuItemRequest request) {
        return new MenuItemDTO(
                request.name(),
                request.description(),
                request.price(),
                request.availableOnlyInRestaurant(),
                request.imagePath(),
                request.restaurantId()
        );
    }

    public MenuItemResponse toResponse(MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isAvailableOnlyInRestaurant(),
                menuItem.getImagePath()
        );
    }

    public List<MenuItemResponse> toResponseList(
            List<MenuItem> menuItems
    ) {
        return menuItems.stream()
                .map(this::toResponse)
                .toList();
    }
}
