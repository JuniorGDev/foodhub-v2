package br.com.foodhub.application.mapper;

import br.com.foodhub.domain.model.UserType;
import br.com.foodhub.presentation.response.UserTypeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserTypeMapper {

    public UserTypeResponse toResponse(UserType userType) {
        return new UserTypeResponse(userType.getId(), userType.getName());
    }

    public List<UserTypeResponse> toResponseList(List<UserType> userTypes) {
        return userTypes.stream().map(this::toResponse).toList();
    }
}
