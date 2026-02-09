package com.challenge.voting_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record VotingSessionOpenResponse(
		@Schema(description = "Voting session ID", example = "1")
		Long sessionId,
		@Schema(description = "Agenda title", example = "Reforma do estatuto")
		String agendaTitle
) {
}
