package com.challenge.voting_api.service.result;

import com.challenge.voting_api.config.VotingResultProperties;
import org.springframework.stereotype.Component;

@Component
public class ThresholdVotingResultRule implements VotingResultRule {

	private final VotingResultProperties properties;

	public ThresholdVotingResultRule(final VotingResultProperties properties) {
		this.properties = properties;
	}

	@Override
	public VotingResultStatus decide(final long yesVotes, final long noVotes) {
		if (yesVotes == noVotes) {
			return VotingResultStatus.DRAW;
		}
		final long total = yesVotes + noVotes;
		if (total <= 0) {
			return VotingResultStatus.DRAW;
		}
		final long threshold = properties.approvalThresholdPercent();
		if (yesVotes * 100 > threshold * total) {
			return VotingResultStatus.APPROVED;
		}
		return VotingResultStatus.REPROVED;
	}
}
