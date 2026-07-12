package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.mapper.MenuItemMapper;
import br.com.foodhub.application.service.MenuItemService;
import br.com.foodhub.presentation.request.MenuItemRequest;
import br.com.foodhub.presentation.response.MenuItemResponse;
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
@RequestMapping("/api/v1/menu-items")
@Tag(
        name = "Menu Items",
        description = "Endpoints responsible for managing restaurant menu items."
)
public class MenuItemController {

    private final MenuItemService service;
    private final MenuItemMapper mapper;

    public MenuItemController(
            MenuItemService menuItemService,
            MenuItemMapper menuItemMapper
    ) {
        this.service = menuItemService;
        this.mapper = menuItemMapper;
    }

    @Operation(
            summary = "List all menu items",
            description = "Returns all registered menu items."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Menu items successfully returned"
            )
    })
    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> findAll() {
        return ResponseEntity.ok(
                mapper.toResponseList(service.findAll())
        );
    }

    @Operation(
            summary = "Find menu item by ID",
            description = "Returns a menu item using its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Menu item found",
                    content = @Content(schema = @Schema(implementation = MenuItemResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Menu item not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> findById(

            @Parameter(
                    description = "Menu item identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                mapper.toResponse(service.findById(id))
        );
    }

    @Operation(
            summary = "Create a new menu item",
            description = "Creates a new menu item associated with a restaurant."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Menu item successfully created",
                    content = @Content(schema = @Schema(implementation = MenuItemResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurant not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid menu item business rule",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<MenuItemResponse> save(
            @Valid @RequestBody MenuItemRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        mapper.toResponse(
                                service.save(
                                        mapper.toDTO(request)
                                )
                        )
                );
    }

    @Operation(
            summary = "Update an existing menu item",
            description = "Updates the information of an existing menu item."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Menu item successfully updated",
                    content = @Content(schema = @Schema(implementation = MenuItemResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Menu item or restaurant not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid menu item business rule",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> update(

            @Parameter(
                    description = "Menu item identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,

            @Valid @RequestBody MenuItemRequest request) {

        return ResponseEntity.ok(
                mapper.toResponse(
                        service.update(
                                id,
                                mapper.toDTO(request)
                        )
                )
        );
    }

    @Operation(
            summary = "Delete a menu item",
            description = "Deletes a menu item by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Menu item successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Menu item not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(

            @Parameter(
                    description = "Menu item identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
