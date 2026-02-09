package com.challenge.voting_api.service;

import com.challenge.voting_api.dto.request.VotingSessionCreateRequest;
import com.challenge.voting_api.dto.response.VotingSessionResponse;

public interface VotingSessionService {

	VotingSessionResponse create(Long agendaId, VotingSessionCreateRequest request);
}
