package com.challenge.voting_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgendaCreateRequest(
		@NotBlank
		@Size(max = 500)
		@Schema(description = "Titulo da pauta", example = "Reforma do estatuto")
		String title
) {
}
