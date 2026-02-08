package com.challenge.voting_api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
		String timestamp,
		int status,
		String error,
		String message,
		String path,
		String apiVersion,
		List<ApiErrorDetail> errors
) {
}
