package br.com.foodhub.infrastructure.config;

import br.com.foodhub.domain.exception.InvalidBusinessHoursException;
import br.com.foodhub.domain.exception.InvalidMenuItemPriceException;
import br.com.foodhub.domain.exception.InvalidRestaurantOwnerException;
import br.com.foodhub.domain.exception.ResourceInUseException;
import br.com.foodhub.domain.exception.ResourceNotFoundException;
import br.com.foodhub.domain.exception.UserAlreadyExistsException;
import br.com.foodhub.domain.exception.UserTypeAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ProblemDetail createProblemDetail(
            HttpStatus status,
            String title,
            String detail
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        return problemDetail;
    }

    private ResponseEntity<ProblemDetail> buildResponse(
            HttpStatus status,
            String title,
            String detail
    ) {
        return ResponseEntity
                .status(status)
                .body(createProblemDetail(status, title, detail));
    }

    @ExceptionHandler(UserTypeAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserTypeAlreadyExists(UserTypeAlreadyExistsException e) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "User type already exists",
                e.getMessage()
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "User already exists",
                e.getMessage()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException e) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .toList();

        ProblemDetail problem = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "Invalid request fields"
        );

        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(InvalidMenuItemPriceException.class)
    public ResponseEntity<ProblemDetail> handleInvalidMenuItemPrice(InvalidMenuItemPriceException e) {
        return buildResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Invalid menu item price",
                e.getMessage()
        );
    }

    @ExceptionHandler(InvalidBusinessHoursException.class)
    public ResponseEntity<ProblemDetail> handleInvalidBusinessHours(InvalidBusinessHoursException e) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid business hours",
                e.getMessage()
        );
    }

    @ExceptionHandler(InvalidRestaurantOwnerException.class)
    public ResponseEntity<ProblemDetail> handleInvalidRestaurantOwner(InvalidRestaurantOwnerException e) {
        return buildResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Invalid restaurant owner",
                e.getMessage()
        );
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<ProblemDetail> handleResourceInUse(ResourceInUseException ex) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "Resource in use",
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                "Invalid request fields"
        );
    }
}
