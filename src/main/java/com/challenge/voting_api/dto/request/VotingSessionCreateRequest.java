package com.challenge.voting_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record VotingSessionCreateRequest(
		@Schema(description = "Duracao da sessao em minutos (opcional)", example = "10")
		Integer durationMinutes) {
}
