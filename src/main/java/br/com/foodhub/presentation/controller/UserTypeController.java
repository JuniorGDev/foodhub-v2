package br.com.foodhub.presentation.controller;

import br.com.foodhub.application.mapper.UserTypeMapper;
import br.com.foodhub.application.service.UserTypeService;
import br.com.foodhub.presentation.request.UserTypeRequest;
import br.com.foodhub.presentation.response.UserTypeResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-types")
@Validated
public class UserTypeController {

    private final UserTypeService service;
    private final UserTypeMapper mapper;

    public UserTypeController(UserTypeService userTypeService,UserTypeMapper userTypeMapper) {
        this.service = userTypeService;
        this.mapper = userTypeMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserTypeResponse>> findAll() {
        return ResponseEntity.ok(mapper.toResponseList(service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTypeResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<UserTypeResponse> save(@Valid @RequestBody UserTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(service.save(request.name())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserTypeResponse> update(@PathVariable UUID id, @Valid @RequestBody UserTypeRequest request) {
        return ResponseEntity.ok(mapper.toResponse(service.update(id, request.name())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
