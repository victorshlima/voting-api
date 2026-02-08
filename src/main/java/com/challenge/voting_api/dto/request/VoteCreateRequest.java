package com.challenge.voting_api.dto.request;

import com.challenge.voting_api.dto.VoteChoice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VoteCreateRequest(
		@NotNull
		@Schema(description = "Identificador do associado (UUID)", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
		UUID associateId,
		@NotNull
		@Schema(description = "Voto (SIM/NAO ou YES/NO)", example = "SIM", allowableValues = {"SIM", "NAO", "YES", "NO"})
		VoteChoice vote
) {
}
