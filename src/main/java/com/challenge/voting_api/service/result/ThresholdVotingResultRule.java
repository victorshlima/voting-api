package com.challenge.voting_api.service.result;

import com.challenge.voting_api.config.VotingResultProperties;
import org.springframework.stereotype.Component;

@Component
public class ThresholdVotingResultRule implements VotingResultRule {

	private final VotingResultProperties properties;

	public ThresholdVotingResultRule(final VotingResultProperties properties) {
		this.properties = properties;
	}

	static final int ONE_HUNDRED = 100;
	static final int MIN_THRESHOLD = 0;

	@Override
	public VotingResultStatus decide(final long yesVotes, final long noVotes) {
		if (yesVotes == noVotes) {
			return VotingResultStatus.DRAW;
		}
		final long total = yesVotes + noVotes;
		if (total <= MIN_THRESHOLD) {
			return VotingResultStatus.DRAW;
		}
		final long threshold = properties.approvalThresholdPercent();
		if (yesVotes * ONE_HUNDRED > threshold * total) {
			return VotingResultStatus.APPROVED;
		}
		return VotingResultStatus.REPROVED;
	}
}
