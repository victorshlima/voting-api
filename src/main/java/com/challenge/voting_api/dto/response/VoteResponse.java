package com.challenge.voting_api.dto.response;

import com.challenge.voting_api.dto.VoteChoice;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record VoteResponse(
		@Schema(description = "Vote ID", example = "1")
		Long id,
		@Schema(description = "Voting session ID", example = "1")
		Long votingSessionId,
		@Schema(description = "Associate ID", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
		UUID associateId,
		@Schema(description = "Vote value", example = "SIM")
		VoteChoice vote
) {
}
