package com.challenge.voting_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record VotingSessionResponse(
		@Schema(description = "Session ID", example = "1")
		Long sessionId,
		@Schema(description = "Session start", example = "2025-01-01T00:01:00Z")
		OffsetDateTime startsAt,
		@Schema(description = "Session end (startsDate + duration)", example = "2025-01-01T00:10:00Z")
		OffsetDateTime endsAt
) {
}
