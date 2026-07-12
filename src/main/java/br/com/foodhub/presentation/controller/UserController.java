package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.mapper.UserMapper;
import br.com.foodhub.application.service.UserService;
import br.com.foodhub.presentation.request.UserRequest;
import br.com.foodhub.presentation.request.UserUpdateRequest;
import br.com.foodhub.presentation.response.UserResponse;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(
        name = "Users",
        description = "Endpoints responsible for managing users."
)
public class UserController {

    private final UserMapper mapper;
    private final UserService service;

    public UserController(UserMapper mapper, UserService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @Operation(
            summary = "List all users",
            description = "Returns all registered users."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users successfully returned"
            )
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(
                mapper.toResponseList(service.findAll())
        );
    }

    @Operation(
            summary = "Find user by ID",
            description = "Returns a user using its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(

            @Parameter(
                    description = "User identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                mapper.toResponse(
                        service.findById(id)
                )
        );
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user in the system."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully created",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User type not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already registered",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<UserResponse> save(
            @Valid @RequestBody UserRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        mapper.toResponse(
                                service.save(
                                        mapper.toCreateDTO(request)
                                )
                        )
                );
    }

    @Operation(
            summary = "Update an existing user",
            description = "Updates the information of an existing user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or user type not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already registered",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(

            @Parameter(
                    description = "User identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,

            @Valid @RequestBody UserUpdateRequest request) {

        return ResponseEntity.ok(
                mapper.toResponse(
                        service.update(
                                id,
                                mapper.toUpdateDTO(request)
                        )
                )
        );
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(

            @Parameter(
                    description = "User identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
