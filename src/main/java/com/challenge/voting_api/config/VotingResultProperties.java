package com.challenge.voting_api.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "voting-result")
@Validated
public record VotingResultProperties(
		@Min(0)
		@Max(100)
		int approvalThresholdPercent,
		@NotNull
		Duration schedulerInterval
) {
}
