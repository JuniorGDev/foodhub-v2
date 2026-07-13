package br.com.foodhub.application.service;

import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.exception.UserTypeAlreadyExistsException;
import br.com.foodhub.domain.model.UserType;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserTypeService Tests")
class UserTypeServiceTest {

    @Mock
    private UserTypeRepository userTypeRepository;

    @InjectMocks
    private UserTypeService userTypeService;

    private UUID userTypeId;
    private UserType userType;
    private String userTypeName;

    @BeforeEach
    void setUp() {
        userTypeId = UUID.randomUUID();
        userTypeName = "OWNER";
        userType = createUserType();
    }

    @Nested
    @DisplayName("Create UserType")
    class CreateUserTypeTests {

        @Test
        @DisplayName("Should create user type successfully")
        void shouldCreateUserTypeSuccessfully() {
            // Given
            when(userTypeRepository.existsByName(anyString())).thenReturn(false);
            when(userTypeRepository.save(any(UserType.class))).thenReturn(userType);

            // When
            UserType result = userTypeService.save(userTypeName);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(userTypeName.toUpperCase());
            
            verify(userTypeRepository).existsByName(anyString());
            verify(userTypeRepository).save(any(UserType.class));
        }

        @Test
        @DisplayName("Should throw exception when name already exists")
        void shouldThrowExceptionWhenNameAlreadyExists() {
            // Given
            when(userTypeRepository.existsByName(anyString())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userTypeService.save(userTypeName))
                    .isInstanceOf(UserTypeAlreadyExistsException.class)
                    .hasMessageContaining("User type already exists with name: " + userTypeName.toUpperCase());

            verify(userTypeRepository).existsByName(anyString());
            verify(userTypeRepository, never()).save(any(UserType.class));
        }
    }

    @Nested
    @DisplayName("Find UserType By ID")
    class FindUserTypeByIdTests {

        @Test
        @DisplayName("Should find user type by ID")
        void shouldFindUserTypeById() {
            // Given
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(userType));

            // When
            UserType result = userTypeService.findById(userTypeId);

            // Then
            assertThat(result).isNotNull();
            
            verify(userTypeRepository).findById(userTypeId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user type not found")
        void shouldThrowResourceNotFoundExceptionWhenUserTypeNotFound() {
            // Given
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userTypeService.findById(userTypeId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User type not found with id: " + userTypeId);

            verify(userTypeRepository).findById(userTypeId);
        }
    }

    @Nested
    @DisplayName("Update UserType")
    class UpdateUserTypeTests {

        @Test
        @DisplayName("Should update user type successfully")
        void shouldUpdateUserTypeSuccessfully() {
            // Given
            String newName = "ADMIN";
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(userType));
            when(userTypeRepository.existsByNameAndIdNot(eq(newName), eq(userTypeId))).thenReturn(false);
            when(userTypeRepository.save(any(UserType.class))).thenReturn(userType);

            // When
            UserType result = userTypeService.update(userTypeId, newName);

            // Then
            assertThat(result).isNotNull();
            
            verify(userTypeRepository).findById(userTypeId);
            verify(userTypeRepository).existsByNameAndIdNot(eq(newName), eq(userTypeId));
            verify(userTypeRepository).save(any(UserType.class));
        }

        @Test
        @DisplayName("Should throw exception when name already exists for another user type")
        void shouldThrowExceptionWhenNameAlreadyExistsForAnotherUserType() {
            // Given
            String newName = "ADMIN";
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(userType));
            when(userTypeRepository.existsByNameAndIdNot(eq(newName), eq(userTypeId))).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userTypeService.update(userTypeId, newName))
                    .isInstanceOf(UserTypeAlreadyExistsException.class)
                    .hasMessageContaining("User type already exists with name: " + newName.toUpperCase());

            verify(userTypeRepository).findById(userTypeId);
            verify(userTypeRepository).existsByNameAndIdNot(eq(newName), eq(userTypeId));
            verify(userTypeRepository, never()).save(any(UserType.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user type not found during update")
        void shouldThrowResourceNotFoundExceptionWhenUserTypeNotFoundDuringUpdate() {
            // Given
            String newName = "ADMIN";
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userTypeService.update(userTypeId, newName))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User type not found with id: " + userTypeId);

            verify(userTypeRepository).findById(userTypeId);
            verify(userTypeRepository, never()).existsByNameAndIdNot(anyString(), any(UUID.class));
            verify(userTypeRepository, never()).save(any(UserType.class));
        }
    }

    @Nested
    @DisplayName("Delete UserType")
    class DeleteUserTypeTests {

        @Test
        @DisplayName("Should delete user type successfully")
        void shouldDeleteUserTypeSuccessfully() {
            // Given
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(userType));
            doNothing().when(userTypeRepository).delete(userType);

            // When
            userTypeService.delete(userTypeId);

            // Then
            verify(userTypeRepository).findById(userTypeId);
            verify(userTypeRepository).delete(userType);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user type not found during delete")
        void shouldThrowResourceNotFoundExceptionWhenUserTypeNotFoundDuringDelete() {
            // Given
            when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userTypeService.delete(userTypeId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User type not found with id: " + userTypeId);

            verify(userTypeRepository).findById(userTypeId);
            verify(userTypeRepository, never()).delete(any(UserType.class));
        }
    }

    // Private helper methods for creating test objects

    private UserType createUserType() {
        return new UserType("OWNER");
    }
}
