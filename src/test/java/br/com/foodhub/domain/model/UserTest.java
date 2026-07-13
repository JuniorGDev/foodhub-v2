package br.com.foodhub.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Domain Tests")
class UserTest {

    private UserType userType;

    @BeforeEach
    void setUp() {
        userType = new UserType("USER");
    }

    @Nested
    @DisplayName("Update User")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user")
        void shouldUpdateUser() {
            // Given
            User user = User.create(
                    "Old Name",
                    "old@example.com",
                    "password123",
                    "Old Address",
                    userType
            );

            String newName = "New Name";
            String newEmail = "new@example.com";
            String newAddress = "New Address";
            UserType newUserType = new UserType("ADMIN");

            // When
            user.update(newName, newEmail, newAddress, newUserType);

            // Then
            assertThat(user.getName()).isEqualTo(newName);
            assertThat(user.getEmail()).isEqualTo(newEmail.toLowerCase());
            assertThat(user.getAddress()).isEqualTo(newAddress);
            assertThat(user.getUserType()).isEqualTo(newUserType);
            assertThat(user.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Email Normalization")
    class EmailNormalizationTests {

        @Test
        @DisplayName("Should normalize email to lowercase")
        void shouldNormalizeEmailToLowercase() {
            // Given
            String uppercaseEmail = "USER@EXAMPLE.COM";
            String mixedCaseEmail = "UsEr@ExAmPlE.CoM";

            // When
            User user1 = User.create(
                    "User 1",
                    uppercaseEmail,
                    "password123",
                    "Address 1",
                    userType
            );

            User user2 = User.create(
                    "User 2",
                    mixedCaseEmail,
                    "password123",
                    "Address 2",
                    userType
            );

            // Then
            assertThat(user1.getEmail()).isEqualTo("user@example.com");
            assertThat(user2.getEmail()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("Should trim email whitespace")
        void shouldTrimEmailWhitespace() {
            // Given
            String emailWithSpaces = "  user@example.com  ";

            // When
            User user = User.create(
                    "User",
                    emailWithSpaces,
                    "password123",
                    "Address",
                    userType
            );

            // Then
            assertThat(user.getEmail()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("Should normalize email during update")
        void shouldNormalizeEmailDuringUpdate() {
            // Given
            User user = User.create(
                    "User",
                    "old@example.com",
                    "password123",
                    "Address",
                    userType
            );

            String newEmail = "  NEW@EXAMPLE.COM  ";

            // When
            user.update("User", newEmail, "Address", userType);

            // Then
            assertThat(user.getEmail()).isEqualTo("new@example.com");
        }
    }

    @Nested
    @DisplayName("Password Change")
    class PasswordChangeTests {

        @Test
        @DisplayName("Should change password")
        void shouldChangePassword() {
            // Given
            User user = User.create(
                    "User",
                    "user@example.com",
                    "oldPassword",
                    "Address",
                    userType
            );

            String newPassword = "newPassword";

            // When
            User updatedUser = User.create(
                    user.getName(),
                    user.getEmail(),
                    newPassword,
                    user.getAddress(),
                    user.getUserType()
            );

            // Then
            assertThat(updatedUser.getPassword()).isEqualTo(newPassword);
        }
    }

    @Nested
    @DisplayName("Owner Check")
    class OwnerCheckTests {

        @Test
        @DisplayName("Should return true when user is OWNER")
        void shouldReturnTrueWhenUserIsOwner() {
            // Given
            UserType ownerType = new UserType("OWNER");
            User user = User.create(
                    "Owner",
                    "owner@example.com",
                    "password123",
                    "Address",
                    ownerType
            );

            // When
            boolean isOwner = user.isOwner();

            // Then
            assertThat(isOwner).isTrue();
        }

        @Test
        @DisplayName("Should return false when user is not OWNER")
        void shouldReturnFalseWhenUserIsNotOwner() {
            // Given
            UserType userType = new UserType("USER");
            User user = User.create(
                    "User",
                    "user@example.com",
                    "password123",
                    "Address",
                    userType
            );

            // When
            boolean isOwner = user.isOwner();

            // Then
            assertThat(isOwner).isFalse();
        }
    }
}
