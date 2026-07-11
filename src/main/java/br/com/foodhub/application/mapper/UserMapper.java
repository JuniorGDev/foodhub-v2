package br.com.foodhub.application.mapper;

import br.com.foodhub.application.dto.user.CreateUserDTO;
import br.com.foodhub.application.dto.user.UpdateUserDTO;
import br.com.foodhub.domain.model.User;
import br.com.foodhub.presentation.request.UserRequest;
import br.com.foodhub.presentation.request.UserUpdateRequest;
import br.com.foodhub.presentation.response.UserResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    private final UserTypeMapper userTypeMapper;

    public UserMapper(UserTypeMapper userTypeMapper) {
        this.userTypeMapper = userTypeMapper;
    }

    public CreateUserDTO toCreateDTO(UserRequest user) {
        return new CreateUserDTO(
                user.name(),
                user.email(),
                user.password(),
                user.address(),
                user.userTypeId()
        );
    }

    public UpdateUserDTO toUpdateDTO(UserUpdateRequest user) {
        return new UpdateUserDTO(
                user.name(),
                user.email(),
                user.address(),
                user.userTypeId()
        );
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getName(),
                user.getEmail(),
                user.getAddress(),
                userTypeMapper.toResponse(user.getUserType())
        );
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(this::toResponse).toList();
    }
}
