package com.challenge.voting_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgendaCreateRequest(
		@NotBlank
		@Size(max = 500)
		String title
) {
}
