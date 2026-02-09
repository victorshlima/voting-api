package com.challenge.voting_api.exception;

import com.challenge.voting_api.dto.response.ApiErrorDetail;
import com.challenge.voting_api.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.HtmlUtils;

@RestControllerAdvice
@Log4j2
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String API_VERSION_HEADER = "X-API-Version";
	private static final String DEFAULT_API_VERSION = "1";

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request
	) {
		List<ApiErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> new ApiErrorDetail(error.getField(), error.getDefaultMessage()))
				.toList();
		ApiErrorResponse response = buildErrorResponse(status, "Validation failed", request, errors);
		return new ResponseEntity<>(response, headers, status);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request
	) {
		ApiErrorResponse response = buildErrorResponse(status, "Malformed JSON request", request, List.of());
		return new ResponseEntity<>(response, headers, status);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(
			TypeMismatchException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request
	) {
		ApiErrorResponse response = buildErrorResponse(status, "Invalid parameter", request, List.of());
		return new ResponseEntity<>(response, headers, status);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception ex,
			Object body,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request
	) {
		ApiErrorResponse response = buildErrorResponse(status, resolveMessage(status, body), request, List.of());
		return new ResponseEntity<>(response, headers, status);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
			ResponseStatusException ex,
			HttpServletRequest request
	) {
		String message = ex.getReason();
		if (message == null || message.isBlank()) {
			message = resolveMessage(ex.getStatusCode(), null);
		}
		ApiErrorResponse response = buildErrorResponse(ex.getStatusCode(), message, request, List.of());
		return ResponseEntity.status(ex.getStatusCode()).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnhandledException(
			Exception ex,
			HttpServletRequest request
	) {
		log.error("Unhandled exception", ex);
		ApiErrorResponse response = buildErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Unexpected error",
				request,
				List.of()
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	private ApiErrorResponse buildErrorResponse(
			HttpStatusCode status,
			String message,
			WebRequest request,
			List<ApiErrorDetail> errors
	) {
		return buildErrorResponse(status, message, resolvePath(request), resolveApiVersion(request), errors);
	}

	private ApiErrorResponse buildErrorResponse(
			HttpStatusCode status,
			String message,
			HttpServletRequest request,
			List<ApiErrorDetail> errors
	) {
		return buildErrorResponse(status, message, request.getRequestURI(), resolveApiVersion(request), errors);
	}

	private ApiErrorResponse buildErrorResponse(
			HttpStatusCode status,
			String message,
			String path,
			String apiVersion,
			List<ApiErrorDetail> errors
	) {
		HttpStatus httpStatus = HttpStatus.resolve(status.value());
		String error = httpStatus != null ? httpStatus.getReasonPhrase() : "Unknown Status";
		return new ApiErrorResponse(
				OffsetDateTime.now(ZoneOffset.UTC).toString(),
				status.value(),
				error,
				sanitize(message),
				sanitize(path),
				sanitize(apiVersion),
				sanitizeErrors(errors)
		);
	}

	private String resolvePath(WebRequest request) {
		if (request instanceof ServletWebRequest servletWebRequest) {
			return servletWebRequest.getRequest().getRequestURI();
		}
		return null;
	}

	private String resolveApiVersion(WebRequest request) {
		if (request instanceof ServletWebRequest servletWebRequest) {
			return resolveApiVersion(servletWebRequest.getRequest());
		}
		return DEFAULT_API_VERSION;
	}

	private String resolveApiVersion(HttpServletRequest request) {
		String apiVersion = request.getHeader(API_VERSION_HEADER);
		if (apiVersion == null || apiVersion.isBlank()) {
			return DEFAULT_API_VERSION;
		}
		return apiVersion;
	}

	private String resolveMessage(HttpStatusCode status, Object body) {
		if (body instanceof String bodyString && !bodyString.isBlank()) {
			return bodyString;
		}
		HttpStatus httpStatus = HttpStatus.resolve(status.value());
		if (httpStatus != null) {
			return httpStatus.is5xxServerError() ? "Unexpected error" : httpStatus.getReasonPhrase();
		}
		return "Request failed";
	}

	private String sanitize(String value) {
		if (value == null) {
			return null;
		}
		return HtmlUtils.htmlEscape(value);
	}

	private List<ApiErrorDetail> sanitizeErrors(List<ApiErrorDetail> errors) {
		return errors.stream()
				.map(error -> new ApiErrorDetail(
						sanitize(error.field()),
						sanitize(error.message())
				))
				.toList();
	}
}
