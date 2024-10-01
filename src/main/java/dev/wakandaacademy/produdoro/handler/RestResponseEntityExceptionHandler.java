package dev.wakandaacademy.produdoro.handler;

import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Log4j2
public class RestResponseEntityExceptionHandler {
	@ExceptionHandler(APIException.class)
	public ResponseEntity<ErrorApiResponse> handlerGenericException(APIException ex) {
		return ex.buildErrorResponseEntity();
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorApiResponse> handlerGenericException(Exception ex) {
		log.error("Exception: ", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ErrorApiResponse.builder().description("INTERNAL SERVER ERROR!")
						.message("POR FAVOR INFORME AO ADMINISTRADOR DO SISTEMA!").build());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return errors;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ErrorApiResponse> handlerMissingRequestHeaderException(MissingRequestHeaderException ex) {
		log.error("400 BAD_REQUEST - Exception: ", ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ErrorApiResponse.builder()
						.message("Credencial de autenticação não está presente.").build());
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(MalformedJwtException.class)
	public ResponseEntity<ErrorApiResponse> handlerMalformedJwtException(MalformedJwtException ex) {
		log.error("401 UNAUTHORIZED - Exception: ", ex);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ErrorApiResponse.builder()
						.message("Credencial de autenticação não é válida.").build());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorApiResponse> handlerHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		log.error("400 BAD_REQUEST - Exception: ", ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ErrorApiResponse.builder()
						.message("Requisição inválida!")
						.description("Usuário ou tarefa pode não ter sido informada.").build());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorApiResponse> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		log.error("400 BAD_REQUEST - Exception: ", ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ErrorApiResponse.builder()
						.message("Usuário ou tarefa não foi encontrada.").build());
	}

}
