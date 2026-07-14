package br.com.foodhub.application.service;

import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.exception.UserTypeAlreadyExistsException;
import br.com.foodhub.domain.model.KitchenType;
import br.com.foodhub.infrastructure.repository.KitchenTypeRepository;
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
@DisplayName("KitchenTypeService Tests")
class KitchenTypeServiceTest {

    @Mock
    private KitchenTypeRepository kitchenTypeRepository;

    @InjectMocks
    private KitchenTypeService kitchenTypeService;

    private UUID kitchenTypeId;
    private KitchenType kitchenType;
    private String kitchenTypeName;

    @BeforeEach
    void setUp() {
        kitchenTypeId = UUID.randomUUID();
        kitchenTypeName = "Italian";
        kitchenType = createKitchenType();
    }

    @Nested
    @DisplayName("Create KitchenType")
    class CreateKitchenTypeTests {

        @Test
        @DisplayName("Should create kitchen type successfully")
        void shouldCreateKitchenTypeSuccessfully() {
            // Given
            when(kitchenTypeRepository.existsByName(anyString())).thenReturn(false);
            when(kitchenTypeRepository.save(any(KitchenType.class))).thenReturn(kitchenType);

            // When
            KitchenType result = kitchenTypeService.save(kitchenTypeName);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(kitchenTypeName.toUpperCase());
            
            verify(kitchenTypeRepository).existsByName(anyString());
            verify(kitchenTypeRepository).save(any(KitchenType.class));
        }

        @Test
        @DisplayName("Should throw exception when name already exists")
        void shouldThrowExceptionWhenNameAlreadyExists() {
            // Given
            when(kitchenTypeRepository.existsByName(anyString())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> kitchenTypeService.save(kitchenTypeName))
                    .isInstanceOf(UserTypeAlreadyExistsException.class)
                    .hasMessageContaining("User type already exists with name: " + kitchenTypeName.toUpperCase());

            verify(kitchenTypeRepository).existsByName(anyString());
            verify(kitchenTypeRepository, never()).save(any(KitchenType.class));
        }
    }

    @Nested
    @DisplayName("Find KitchenType By ID")
    class FindKitchenTypeByIdTests {

        @Test
        @DisplayName("Should find kitchen type by ID")
        void shouldFindKitchenTypeById() {
            // Given
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.of(kitchenType));

            // When
            KitchenType result = kitchenTypeService.findById(kitchenTypeId);

            // Then
            assertThat(result).isNotNull();
            
            verify(kitchenTypeRepository).findById(kitchenTypeId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when kitchen type not found")
        void shouldThrowResourceNotFoundExceptionWhenKitchenTypeNotFound() {
            // Given
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> kitchenTypeService.findById(kitchenTypeId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Kitchen type not found with id: " + kitchenTypeId);

            verify(kitchenTypeRepository).findById(kitchenTypeId);
        }
    }

    @Nested
    @DisplayName("Update KitchenType")
    class UpdateKitchenTypeTests {

        @Test
        @DisplayName("Should update kitchen type successfully")
        void shouldUpdateKitchenTypeSuccessfully() {
            // Given
            String newName = "Mexican";
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.of(kitchenType));
            when(kitchenTypeRepository.existsByNameAndIdNot(eq(newName), eq(kitchenTypeId))).thenReturn(false);
            when(kitchenTypeRepository.save(any(KitchenType.class))).thenReturn(kitchenType);

            // When
            KitchenType result = kitchenTypeService.update(kitchenTypeId, newName);

            // Then
            assertThat(result).isNotNull();
            
            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(kitchenTypeRepository).existsByNameAndIdNot(eq(newName), eq(kitchenTypeId));
            verify(kitchenTypeRepository).save(any(KitchenType.class));
        }

        @Test
        @DisplayName("Should throw exception when name already exists for another kitchen type")
        void shouldThrowExceptionWhenNameAlreadyExistsForAnotherKitchenType() {
            // Given
            String newName = "Mexican";
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.of(kitchenType));
            when(kitchenTypeRepository.existsByNameAndIdNot(eq(newName), eq(kitchenTypeId))).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> kitchenTypeService.update(kitchenTypeId, newName))
                    .isInstanceOf(UserTypeAlreadyExistsException.class)
                    .hasMessageContaining("User type already exists with name: " + newName);

            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(kitchenTypeRepository).existsByNameAndIdNot(eq(newName), eq(kitchenTypeId));
            verify(kitchenTypeRepository, never()).save(any(KitchenType.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when kitchen type not found during update")
        void shouldThrowResourceNotFoundExceptionWhenKitchenTypeNotFoundDuringUpdate() {
            // Given
            String newName = "Mexican";
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> kitchenTypeService.update(kitchenTypeId, newName))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Kitchen type not found with id: " + kitchenTypeId);

            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(kitchenTypeRepository, never()).existsByNameAndIdNot(anyString(), any(UUID.class));
            verify(kitchenTypeRepository, never()).save(any(KitchenType.class));
        }
    }

    @Nested
    @DisplayName("Delete KitchenType")
    class DeleteKitchenTypeTests {

        @Test
        @DisplayName("Should delete kitchen type successfully")
        void shouldDeleteKitchenTypeSuccessfully() {
            // Given
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.of(kitchenType));
            doNothing().when(kitchenTypeRepository).delete(kitchenType);

            // When
            kitchenTypeService.delete(kitchenTypeId);

            // Then
            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(kitchenTypeRepository).delete(kitchenType);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when kitchen type not found during delete")
        void shouldThrowResourceNotFoundExceptionWhenKitchenTypeNotFoundDuringDelete() {
            // Given
            when(kitchenTypeRepository.findById(kitchenTypeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> kitchenTypeService.delete(kitchenTypeId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Kitchen type not found with id: " + kitchenTypeId);

            verify(kitchenTypeRepository).findById(kitchenTypeId);
            verify(kitchenTypeRepository, never()).delete(any(KitchenType.class));
        }
    }

    // Private helper methods for creating test objects

    private KitchenType createKitchenType() {
        return new KitchenType("Italian");
    }
}
