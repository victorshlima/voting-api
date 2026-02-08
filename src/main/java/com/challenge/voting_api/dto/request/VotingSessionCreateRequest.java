package com.challenge.voting_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VotingSessionCreateRequest(
		@NotNull
		@Schema(description = "Identificador da pauta", example = "1")
		Long agendaId,
		@Schema(description = "Duracao da sessao em minutos (opcional)", example = "10")
		Integer durationMinutes
) {
}
