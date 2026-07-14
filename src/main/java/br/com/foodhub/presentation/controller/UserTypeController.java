package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.mapper.UserTypeMapper;
import br.com.foodhub.application.service.UserTypeService;
import br.com.foodhub.presentation.request.UserTypeRequest;
import br.com.foodhub.presentation.response.UserTypeResponse;
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
@RequestMapping("/api/v1/user-types")
@Tag(
        name = "User Types",
        description = "Endpoints responsible for managing user types."
)
public class UserTypeController {

    private final UserTypeService service;
    private final UserTypeMapper mapper;

    public UserTypeController(
            UserTypeService userTypeService,
            UserTypeMapper userTypeMapper
    ) {
        this.service = userTypeService;
        this.mapper = userTypeMapper;
    }

    @Operation(
            summary = "List all user types",
            description = "Returns all registered user types."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User types successfully returned")
    })
    @GetMapping
    public ResponseEntity<List<UserTypeResponse>> findAll() {
        return ResponseEntity.ok(
                mapper.toResponseList(service.findAll())
        );
    }

    @Operation(
            summary = "Find user type by ID",
            description = "Returns a specific user type using its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User type found"),
            @ApiResponse(responseCode = "404", description = "User type not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserTypeResponse> findById(

            @Parameter(
                    description = "User type identifier",
                    example = "11111111-1111-1111-1111-111111111111"
            )
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                mapper.toResponse(service.findById(id))
        );
    }

    @Operation(
            summary = "Create a new user type",
            description = "Creates a new user type."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User type successfully created",
                    content = @Content(schema = @Schema(implementation = UserTypeResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "409", description = "User type already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserTypeResponse> save(
            @Valid @RequestBody UserTypeRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        mapper.toResponse(
                                service.save(request.name())
                        )
                );
    }

    @Operation(
            summary = "Update an existing user type",
            description = "Updates the information of an existing user type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User type successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User type not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "User type already exists", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserTypeResponse> update(

            @Parameter(
                    description = "User type identifier",
                    example = "11111111-1111-1111-1111-111111111111"
            )
            @PathVariable UUID id,

            @Valid @RequestBody UserTypeRequest request) {

        return ResponseEntity.ok(
                mapper.toResponse(
                        service.update(id, request.name())
                )
        );
    }

    @Operation(
            summary = "Delete a user type",
            description = "Deletes a user type by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User type successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User type not found", content = @Content),
            @ApiResponse(
                    responseCode = "409",
                    description = "Resource in use",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(

            @Parameter(
                    description = "User type identifier",
                    example = "11111111-1111-1111-1111-111111111111"
            )
            @PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
