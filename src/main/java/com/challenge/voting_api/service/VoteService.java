package com.challenge.voting_api.service;

import com.challenge.voting_api.dto.request.VoteCreateRequest;
import com.challenge.voting_api.dto.response.VoteResponse;

public interface VoteService {

	VoteResponse create(final Long votingSessionId, final VoteCreateRequest request);
}
