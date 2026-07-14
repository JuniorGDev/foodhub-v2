package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.mapper.RestaurantMapper;
import br.com.foodhub.application.service.RestaurantService;
import br.com.foodhub.presentation.request.RestaurantRequest;
import br.com.foodhub.presentation.response.RestaurantResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(
        name = "Restaurants",
        description = "Endpoints responsible for managing restaurants."
)
public class RestaurantController {

    private final RestaurantService service;
    private final RestaurantMapper mapper;

    public RestaurantController(
            RestaurantService restaurantService,
            RestaurantMapper restaurantMapper
    ) {
        this.service = restaurantService;
        this.mapper = restaurantMapper;
    }

    @Operation(
            summary = "List all restaurants",
            description = "Returns all registered restaurants."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurants successfully returned"
            )
    })
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> findAll() {
        return ResponseEntity.ok(
                mapper.toResponseList(service.findAll())
        );
    }

    @Operation(
            summary = "Find restaurant by ID",
            description = "Returns a restaurant using its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurant found",
                    content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurant not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> findById(

            @Parameter(
                    description = "Restaurant identifier",
                    example = "dddddddd-dddd-dddd-dddd-dddddddddddd"
            )
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                mapper.toResponse(service.findById(id))
        );
    }

    @Operation(
            summary = "Create a new restaurant",
            description = "Creates a new restaurant associated with an OWNER user and a kitchen type."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Restaurant successfully created",
                    content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Kitchen type or owner not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "The selected user is not allowed to own a restaurant",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<RestaurantResponse> save(
            @Valid @RequestBody RestaurantRequest restaurantRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        mapper.toResponse(
                                service.save(
                                        mapper.toDTO(restaurantRequest)
                                )
                        )
                );
    }

    @Operation(
            summary = "Update an existing restaurant",
            description = "Updates the information of an existing restaurant."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurant successfully updated",
                    content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurant, kitchen type or owner not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "The selected user is not allowed to own a restaurant",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> update(

            @Parameter(
                    description = "Restaurant identifier",
                    example = "dddddddd-dddd-dddd-dddd-dddddddddddd"
            )
            @PathVariable UUID id,

            @Valid @RequestBody RestaurantRequest restaurantRequest) {

        return ResponseEntity.ok(
                mapper.toResponse(
                        service.update(
                                id,
                                mapper.toDTO(restaurantRequest)
                        )
                )
        );
    }

    @Operation(
            summary = "Delete a restaurant",
            description = "Deletes a restaurant by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Restaurant successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurant not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Resource in use",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(

            @Parameter(
                    description = "Restaurant identifier",
                    example = "dddddddd-dddd-dddd-dddd-dddddddddddd"
            )
            @PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
