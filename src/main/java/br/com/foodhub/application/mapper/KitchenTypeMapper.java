package br.com.foodhub.application.mapper;

import br.com.foodhub.domain.model.KitchenType;
import br.com.foodhub.presentation.response.KitchenTypeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KitchenTypeMapper {

    public KitchenTypeResponse toResponse(KitchenType kitchenType) {
        return new KitchenTypeResponse(
                kitchenType.getId(),
                kitchenType.getName()
        );
    }

    public List<KitchenTypeResponse> toResponseList(List<KitchenType> kitchenTypes) {
        return kitchenTypes.stream()
                .map(this::toResponse)
                .toList();
    }
}
