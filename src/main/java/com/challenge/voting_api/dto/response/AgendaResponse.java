package com.challenge.voting_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AgendaResponse(
		@Schema(description = "ID da pauta", example = "1")
		Long agendaId,
		@Schema(description = "Titulo da pauta", example = "Reforma do estatuto")
		String title
) {
}
