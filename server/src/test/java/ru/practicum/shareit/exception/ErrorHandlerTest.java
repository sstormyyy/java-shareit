package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void shouldHandleEmailAlreadyExistsException() {
        Map<String, String> response = errorHandler.handleEmailAlreadyExistsException(
                new EmailAlreadyExistsException("Email already exists")
        );

        assertThat(response).containsEntry("error", "Email already exists");
        assertStatus(EmailAlreadyExistsException.class, HttpStatus.CONFLICT);
    }

    @Test
    void shouldHandleNotFoundException() {
        Map<String, String> response = errorHandler.handleNotFoundException(
                new NotFoundException("Object not found")
        );

        assertThat(response).containsEntry("error", "Object not found");
        assertStatus(NotFoundException.class, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        Map<String, String> response = errorHandler.handleIllegalArgumentException(
                new IllegalArgumentException("Invalid argument")
        );

        assertThat(response).containsEntry("error", "Invalid argument");
        assertStatus(IllegalArgumentException.class, HttpStatus.BAD_REQUEST);
    }

    private void assertStatus(Class<? extends RuntimeException> exceptionClass,
                              HttpStatus expectedStatus) throws NoSuchMethodException {
        ResponseStatus responseStatus = ErrorHandler.class
                .getMethod(getHandlerMethodName(exceptionClass), exceptionClass)
                .getAnnotation(ResponseStatus.class);

        assertThat(responseStatus).isNotNull();
        assertThat(responseStatus.value()).isEqualTo(expectedStatus);
    }

    private String getHandlerMethodName(Class<? extends RuntimeException> exceptionClass) {
        if (exceptionClass == EmailAlreadyExistsException.class) {
            return "handleEmailAlreadyExistsException";
        }
        if (exceptionClass == NotFoundException.class) {
            return "handleNotFoundException";
        }
        return "handleIllegalArgumentException";
    }
}