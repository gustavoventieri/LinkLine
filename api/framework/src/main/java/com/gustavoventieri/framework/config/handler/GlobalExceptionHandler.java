package com.gustavoventieri.framework.config.handler;

import org.gustavoventieri.domain.exception.*;  
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global para tratamento de exceções na aplicação.
 * Captura exceções específicas e gerais lançadas pelos controllers,
 * formatando respostas HTTP padronizadas em JSON contendo informações úteis.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Cria o corpo da resposta para exceções padrão.
     *
     * @param status  o status HTTP da resposta
     * @param message a mensagem de erro
     * @return um mapa contendo timestamp, status, tipo de erro e mensagem
     */
    private Map<String, Object> buildBody(HttpStatus status, String message) {
        return Map.of(
            "timestamp", Instant.now().toString(),
            "status", status.value(),
            "error", status.getReasonPhrase(),
            "message", message
        );
    }

    /**
     * Cria o corpo da resposta para erros de validação.
     *
     * @param status o status HTTP da resposta
     * @param errors mapa de erros de validação, onde a chave é o nome do campo e o valor é a mensagem
     * @return um mapa contendo timestamp, status, tipo de erro, mensagem geral e detalhes dos erros de validação
     */
    private Map<String, Object> buildValidationBody(HttpStatus status, Map<String, String> errors) {
        return Map.of(
            "timestamp", Instant.now().toString(),
            "status", status.value(),
            "error", status.getReasonPhrase(),
            "message", "Validation failed",
            "errors", errors
        );
    }

    /**
     * Trata exceções lançadas quando a validação dos argumentos do método falha.
     *
     * @param ex exceção contendo detalhes dos erros de validação
     * @return resposta HTTP com status 400 (Bad Request) contendo detalhes dos erros de validação
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildValidationBody(HttpStatus.BAD_REQUEST, errors));
    }

    /**
     * Trata exceções do tipo BadRequest.
     *
     * @param ex exceção BadRequest
     * @return resposta HTTP com status 400 (Bad Request) e mensagem da exceção
     */
    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<?> handleBadRequest(BadRequest ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildBody(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * Trata exceções relacionadas a erros JWT.
     *
     * @param ex exceção JWTException
     * @return resposta HTTP com status 500 (Internal Server Error) e mensagem da exceção
     */
    @ExceptionHandler(JWTException.class)
    public ResponseEntity<?> handleJWT(JWTException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    /**
     * Trata exceções do tipo Conflict.
     *
     * @param ex exceção Conflict
     * @return resposta HTTP com status 409 (Conflict) e mensagem da exceção
     */
    @ExceptionHandler(Conflict.class)
    public ResponseEntity<?> handleConflict(Conflict ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    /**
     * Trata exceções do tipo Expired.
     *
     * @param ex exceção Expired
     * @return resposta HTTP com status 401 (Unauthorized) e mensagem da exceção
     */
    @ExceptionHandler(Expired.class)
    public ResponseEntity<?> handleExpired(Expired ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildBody(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    /**
     * Trata exceções do tipo InternalServerError.
     *
     * @param ex exceção InternalServerError
     * @return resposta HTTP com status 500 (Internal Server Error) e mensagem da exceção
     */
    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<?> handleInternalServerError(InternalServerError ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    /**
     * Trata exceções do tipo InvalidData.
     *
     * @param ex exceção InvalidData
     * @return resposta HTTP com status 422 (Unprocessable Entity) e mensagem da exceção
     */
    @ExceptionHandler(InvalidData.class)
    public ResponseEntity<?> handleInvalidData(InvalidData ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildBody(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage()));
    }

    /**
     * Trata exceções do tipo NotFound.
     *
     * @param ex exceção NotFound
     * @return resposta HTTP com status 404 (Not Found) e mensagem da exceção
     */
    @ExceptionHandler(NotFound.class)
    public ResponseEntity<?> handleNotFound(NotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * Trata exceções do tipo RequestTimeout.
     *
     * @param ex exceção RequestTimeout
     * @return resposta HTTP com status 408 (Request Timeout) e mensagem da exceção
     */
    @ExceptionHandler(RequestTimeout.class)
    public ResponseEntity<?> handleRequestTimeout(RequestTimeout ex) {
        return ResponseEntity
                .status(HttpStatus.REQUEST_TIMEOUT)
                .body(buildBody(HttpStatus.REQUEST_TIMEOUT, ex.getMessage()));
    }

    /**
     * Trata exceções do tipo Unauthorized.
     *
     * @param ex exceção Unauthorized
     * @return resposta HTTP com status 401 (Unauthorized) e mensagem da exceção
     */
    @ExceptionHandler(Unauthorized.class)
    public ResponseEntity<?> handleUnauthorized(Unauthorized ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildBody(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    /**
     * Trata todas as outras exceções não específicas.
     *
     * @param ex exceção genérica
     * @return resposta HTTP com status 500 (Internal Server Error) e mensagem padrão de erro inesperado
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + ex.getMessage()));
    }
}
