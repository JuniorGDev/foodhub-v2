package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.mapper.KitchenTypeMapper;
import br.com.foodhub.application.service.KitchenTypeService;
import br.com.foodhub.presentation.request.KitchenTypeRequest;
import br.com.foodhub.presentation.response.KitchenTypeResponse;
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
@RequestMapping("/api/v1/kitchen-types")
@Tag(
        name = "Kitchen Types",
        description = "Endpoints responsible for managing kitchen types."
)
public class KitchenTypeController {

    private final KitchenTypeService service;
    private final KitchenTypeMapper mapper;

    public KitchenTypeController(
            KitchenTypeService service,
            KitchenTypeMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(
            summary = "List all kitchen types",
            description = "Returns all registered kitchen types."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Kitchen types successfully returned"
            )
    })
    @GetMapping
    public ResponseEntity<List<KitchenTypeResponse>> findAll() {
        return ResponseEntity.ok(
                mapper.toResponseList(service.findAll())
        );
    }

    @Operation(
            summary = "Find kitchen type by ID",
            description = "Returns a kitchen type using its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Kitchen type found",
                    content = @Content(
                            schema = @Schema(implementation = KitchenTypeResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Kitchen type not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<KitchenTypeResponse> findById(

            @Parameter(
                    description = "Kitchen type identifier",
                    example = "44444444-4444-4444-4444-444444444444"
            )
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                mapper.toResponse(
                        service.findById(id)
                )
        );
    }

    @Operation(
            summary = "Create a new kitchen type",
            description = "Creates a new kitchen type."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Kitchen type successfully created",
                    content = @Content(
                            schema = @Schema(implementation = KitchenTypeResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Kitchen type already exists",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<KitchenTypeResponse> save(
            @Valid @RequestBody KitchenTypeRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        mapper.toResponse(
                                service.save(request.name())
                        )
                );
    }

    @Operation(
            summary = "Update an existing kitchen type",
            description = "Updates an existing kitchen type."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Kitchen type successfully updated",
                    content = @Content(
                            schema = @Schema(implementation = KitchenTypeResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Kitchen type not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Kitchen type already exists",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<KitchenTypeResponse> update(

            @Parameter(
                    description = "Kitchen type identifier",
                    example = "44444444-4444-4444-4444-444444444444"
            )
            @PathVariable UUID id,

            @Valid @RequestBody KitchenTypeRequest request) {

        return ResponseEntity.ok(
                mapper.toResponse(
                        service.update(id, request.name())
                )
        );
    }

    @Operation(
            summary = "Delete a kitchen type",
            description = "Deletes a kitchen type by its identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Kitchen type successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Kitchen type not found",
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
                    description = "Kitchen type identifier",
                    example = "44444444-4444-4444-4444-444444444444"
            )
            @PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
