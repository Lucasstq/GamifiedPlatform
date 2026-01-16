package dev.gamified.GamifiedPlatform.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Exceção genérica retorna mensagem genérica e errorId")
    void testHandleGenericException() {
        Exception ex = new Exception("Erro inesperado");
        ResponseEntity<Map<String, Object>> resp = handler.handleGenericException(ex, null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertTrue(resp.getBody().containsKey("errorId"));
        assertTrue(resp.getBody().get("message").toString().contains("error ID"));
    }
}

