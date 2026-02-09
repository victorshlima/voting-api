package com.challenge.voting_api.service;

import com.challenge.voting_api.dto.request.AgendaCreateRequest;
import com.challenge.voting_api.dto.response.AgendaResponse;

public interface AgendaService {

	AgendaResponse createAgenda(AgendaCreateRequest request);
}
