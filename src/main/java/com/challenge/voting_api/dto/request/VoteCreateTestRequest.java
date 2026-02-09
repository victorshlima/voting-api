package com.challenge.voting_api.dto.request;

import com.challenge.voting_api.dto.VoteChoice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record VoteCreateTestRequest(
		@NotNull
		@Schema(description = "Voto (SIM/NAO)", example = "SIM",
				allowableValues = {"SIM", "NAO"})
		VoteChoice vote
) {
}
