package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthControllerTest {

    @Test
    void healthCheckReturnsUp() {
        HealthController controller = new HealthController();

        HealthController.HealthResponse response = controller.healthCheck();

        assertEquals("UP", response.status());
    }
}