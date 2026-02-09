package com.challenge.voting_api.service.result;

public interface VotingResultRule {

	VotingResultStatus decide(long yesVotes, long noVotes);
}
