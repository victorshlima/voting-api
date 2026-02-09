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
		@Min(MIN_APPROVAL_PERCENT)
		@Max(MAX_APPROVAL_PERCENT)
		int approvalThresholdPercent,
		@NotNull
		Duration schedulerInterval
) {
	public static final int MIN_APPROVAL_PERCENT = 0;
	public static final int MAX_APPROVAL_PERCENT = 100;
}
