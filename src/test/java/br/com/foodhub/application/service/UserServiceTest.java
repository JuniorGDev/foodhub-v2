package br.com.foodhub.application.service;

import br.com.foodhub.application.dto.user.CreateUserDTO;
import br.com.foodhub.application.dto.user.UpdateUserDTO;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.exception.UserAlreadyExistsException;
import br.com.foodhub.domain.model.User;
import br.com.foodhub.domain.model.UserType;
import br.com.foodhub.infrastructure.repository.UserRepository;
import br.com.foodhub.infrastructure.repository.UserTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private UUID userTypeId;
    private UserType userType;
    private User user;
    private CreateUserDTO createUserDTO;
    private UpdateUserDTO updateUserDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userTypeId = UUID.randomUUID();
        
        userType = createUserType();
        user = createUser(userType);
        createUserDTO = createCreateUserDTO(userTypeId);
        updateUserDTO = createUpdateUserDTO(userTypeId);
    }

    @Nested
    @DisplayName("Create User")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() {
            // Given
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(userType));
            when(userRepository.existsByEmail(createUserDTO.email())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            User result = userService.save(createUserDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(user.getName());
            assertThat(result.getEmail()).isEqualTo(user.getEmail().toLowerCase());
            
            verify(userTypeRepository).findById(userTypeId);
            verify(userRepository).existsByEmail(createUserDTO.email());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // Given
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(userType));
            when(userRepository.existsByEmail(createUserDTO.email())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.save(createUserDTO))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("User already exists with email: " + createUserDTO.email());

            verify(userTypeRepository).findById(userTypeId);
            verify(userRepository).existsByEmail(createUserDTO.email());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user type not found")
        void shouldThrowResourceNotFoundExceptionWhenUserTypeNotFound() {
            // Given
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.save(createUserDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User type not found with id: " + userTypeId);

            verify(userTypeRepository).findById(userTypeId);
            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Find User By ID")
    class FindUserByIdTests {

        @Test
        @DisplayName("Should find user by ID")
        void shouldFindUserById() {
            // Given
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // When
            User result = userService.findById(userId);

            // Then
            assertThat(result).isNotNull();
            
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user not found")
        void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
            // Given
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.findById(userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + userId);

            verify(userRepository).findById(userId);
        }
    }

    @Nested
    @DisplayName("Update User")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Given
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(userType));
            when(userRepository.existsByEmailAndIdNot(updateUserDTO.email(), userId)).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            User result = userService.update(userId, updateUserDTO);

            // Then
            assertThat(result).isNotNull();
            
            verify(userTypeRepository).findById(userTypeId);
            verify(userRepository).existsByEmailAndIdNot(updateUserDTO.email(), userId);
            verify(userRepository).findById(userId);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists for another user")
        void shouldThrowExceptionWhenEmailAlreadyExistsForAnotherUser() {
            // Given
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(userType));
            when(userRepository.existsByEmailAndIdNot(updateUserDTO.email(), userId)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.update(userId, updateUserDTO))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("User already exists with email: " + updateUserDTO.email());

            verify(userTypeRepository).findById(userTypeId);
            verify(userRepository).existsByEmailAndIdNot(updateUserDTO.email(), userId);
            verify(userRepository, never()).findById(any(UUID.class));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user not found during update")
        void shouldThrowResourceNotFoundExceptionWhenUserNotFoundDuringUpdate() {
            // Given
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(userType));
            when(userRepository.existsByEmailAndIdNot(updateUserDTO.email(), userId)).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.update(userId, updateUserDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + userId);

            verify(userTypeRepository).findById(userTypeId);
            verify(userRepository).existsByEmailAndIdNot(updateUserDTO.email(), userId);
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Delete User")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // Given
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            doNothing().when(userRepository).delete(user);

            // When
            userService.delete(userId);

            // Then
            verify(userRepository).findById(userId);
            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user not found during delete")
        void shouldThrowResourceNotFoundExceptionWhenUserNotFoundDuringDelete() {
            // Given
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.delete(userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + userId);

            verify(userRepository).findById(userId);
            verify(userRepository, never()).delete(any(User.class));
        }
    }

    // Private helper methods for creating test objects

    private UserType createUserType() {
        return new UserType("OWNER");
    }

    private User createUser(UserType userType) {
        return User.create(
                "John Doe",
                "john.doe@example.com",
                "password123",
                "123 Main St",
                userType
        );
    }

    private CreateUserDTO createCreateUserDTO(UUID userTypeId) {
        return new CreateUserDTO(
                "John Doe",
                "john.doe@example.com",
                "password123",
                "123 Main St",
                userTypeId
        );
    }

    private UpdateUserDTO createUpdateUserDTO(UUID userTypeId) {
        return new UpdateUserDTO(
                "Jane Doe",
                "jane.doe@example.com",
                "456 Oak Ave",
                userTypeId
        );
    }
}
