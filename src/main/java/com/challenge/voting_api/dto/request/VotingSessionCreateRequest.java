package com.challenge.voting_api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record VotingSessionCreateRequest(
		@Schema(description = "Duracao da sessao em minutos (opcional)", example = "10")
		Integer durationMinutes) {
}
