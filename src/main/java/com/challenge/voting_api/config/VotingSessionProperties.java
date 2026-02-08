package com.challenge.voting_api.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "voting-session")
@Validated
public record VotingSessionProperties(
		@Min(1)
		int defaultDurationMinutes,
		int maxDurationMinutes
) {
}
